package com.logan.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.security.Security;
import java.security.SignatureException;
import java.util.Iterator;


/*
 * 本工具类代码有复制使用其他作者的代码
 */
public class PGPUtils {

    private void initBCProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }


    // ref: https://github.com/bcgit/bc-java/blob/master/pg/src/main/java/org/bouncycastle/openpgp/examples/PGPExampleUtil.java
    public PGPPublicKey readPublicKey(String keyFile) throws IOException, PGPException {

        InputStream in = new FileInputStream(keyFile);
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(in), new BcKeyFingerprintCalculator());
        Iterator keyRingIter = pgpPub.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();

            Iterator keyIter = keyRing.getPublicKeys();
            while (keyIter.hasNext()) {
                PGPPublicKey key = (PGPPublicKey) keyIter.next();

                if (key.isEncryptionKey()) {
//                    LogUtils.error("Read Public Key File Successful!");
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find public key in key ring.");
    }


    public static PGPSecretKey readSecretKey(String fileName) throws IOException, PGPException {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
        PGPSecretKey secKey = readSecretKey(keyIn);
        keyIn.close();
        return secKey;
    }


    /**
     * A simple routine that opens a key ring file and loads the first available key
     * suitable for signature generation.
     *
     * @param input stream to read the secret key ring collection from.
     * @return a secret key.
     * @throws IOException  on a problem with using the input stream.
     * @throws PGPException if there is an issue parsing the input stream.
     */
    public static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());
        Iterator keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
            Iterator keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = (PGPSecretKey) keyIter.next();
                if (key.isSigningKey()) {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }


    public String decryptRequest(String requestString, PGPPrivateKey receiverPrivateKey, PGPPublicKey senderPublicKey)
            throws PGPException, SignatureException, IOException {
        byte inBytes[] = requestString.getBytes();
        ByteArrayOutputStream decryptedMessage = new ByteArrayOutputStream();
        decryptVerifyMessage(new ByteArrayInputStream(inBytes), decryptedMessage, receiverPrivateKey, senderPublicKey);
        return decryptedMessage.toString();
    }


    private void decryptVerifyMessage(InputStream inputStream, OutputStream outputStream,
                                      PGPPrivateKey receiverPrivateKey, PGPPublicKey senderPublicKey)
            throws IOException, PGPException, SignatureException {
        initBCProvider();
        inputStream = PGPUtil.getDecoderStream(inputStream);
        PGPObjectFactory objectFactory = new PGPObjectFactory(inputStream, new BcKeyFingerprintCalculator());
        PGPEncryptedDataList encryptedDataList;

        Object obj = objectFactory.nextObject();
        //
        // the first object might be a PGP marker packet.
        //
        if (obj instanceof PGPEncryptedDataList) {
            encryptedDataList = (PGPEncryptedDataList) obj;
        } else {
            encryptedDataList = (PGPEncryptedDataList) objectFactory.nextObject();
        }

        Iterator encryptedDataIterator = encryptedDataList.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;

        if (encryptedDataIterator.hasNext()) {
            publicKeyEncryptedData = (PGPPublicKeyEncryptedData) encryptedDataIterator.next();
        }
        InputStream clear = publicKeyEncryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(receiverPrivateKey));
        PGPObjectFactory plainFact = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());
        Object message;
        PGPOnePassSignatureList onePassSignatureList = null;
        PGPSignatureList signatureList = null;
        PGPCompressedData compressedData;

        message = plainFact.nextObject();
        ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
        while (message != null) {
            if (message instanceof PGPCompressedData) {
                compressedData = (PGPCompressedData) message;
                plainFact = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
                message = plainFact.nextObject();
            }
            if (message instanceof PGPLiteralData) {
                // have to read it and keep it somewhere.
                Streams.pipeAll(((PGPLiteralData) message).getInputStream(), actualOutput);
            } else if (message instanceof PGPOnePassSignatureList) {
                onePassSignatureList = (PGPOnePassSignatureList) message;
            } else if (message instanceof PGPSignatureList) {
                signatureList = (PGPSignatureList) message;
            } else {
                throw new PGPException("message unknown message type.");
            }
            message = plainFact.nextObject();
        }
        actualOutput.close();

        byte[] output = actualOutput.toByteArray();
        if (onePassSignatureList == null || signatureList == null) {
            throw new PGPException("Poor PGP. Signatures not found.");
        } else {
            for (int i = 0; i < onePassSignatureList.size(); i++) {
                PGPOnePassSignature ops = onePassSignatureList.get(0);
                if (senderPublicKey != null) {
                    ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider(BouncyCastleProvider.PROVIDER_NAME), senderPublicKey);
                    ops.update(output);
                    PGPSignature signature = signatureList.get(i);
                    if (ops.verify(signature)) {
                        Iterator<?> userIds = senderPublicKey.getUserIDs();
                        while (userIds.hasNext()) {
                            String userId = (String) userIds.next();
                            // 不做签发人的验证
//                            LogUtils.info(String.format("Signed by: {%s}", userId));
                        }
                    } else {
                        throw new SignatureException("Signature verification failed");
                    }
                }
            }
        }

        if (publicKeyEncryptedData.isIntegrityProtected() && !publicKeyEncryptedData.verify()) {
            throw new PGPException("Data is integrity protected but integrity is lost.");
        } else if (senderPublicKey == null) {
            throw new SignatureException("Signature not found");
        } else {
            outputStream.write(output);
            outputStream.flush();
            outputStream.close();
        }
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String decode(String privateContent) {
        try {
            String passPhrase2 = "12345678-2";
            boolean isAscFile = true;
            String keyTempPath = LocalFileUtils.mkTempDir("key");

            PGPUtils pgpUtils = new PGPUtils();
            PGPPublicKey publicKey = null;
            PGPPrivateKey privateKey2 = null;
            if (isAscFile) {
                publicKey = pgpUtils.readPublicKey(keyTempPath + "pub.asc");
                PGPSecretKey pgpSecretKey2 = PGPUtils.readSecretKey(keyTempPath + "secret2.asc");
                privateKey2 = pgpSecretKey2.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder()
                        .setProvider("BC").build(passPhrase2.toCharArray()));
            }
            // user2 使用自己的私钥和 user的公钥解密
            String decryptContent = pgpUtils.decryptRequest(privateContent, privateKey2, publicKey);
            return decryptContent;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("decode res: Invalid key, " + e.getMessage());
            return null;
        }
    }

}
