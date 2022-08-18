package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.model.PhotoFileInfo;
import com.logan.utils.LogUtils;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:28
 */
public class ViewGridPaneCtrl {

    // photosPath 为压缩图片绝对路径
    public GridPane photosViewPane(GridPane pane, List<String> photosPath) {
        CacheData.setAppStatus(SysConfig.RENDER_PREVIEW);
        CacheData.refreshStatus();
        long start = System.currentTimeMillis();
        GridPane gridPane = pane;
        if (gridPane == null) {
            gridPane = new GridPane();
        }
        if (photosPath.size() == 0) {
            // 清除数据后直接返回
            gridPane.getChildren().clear();
            CacheData.setAppStatus(SysConfig.DEFAULT);
            CacheData.refreshStatus();
            return pane;
        }

        int row = 0;
        int column = 0;
        for (int i = 0; i < photosPath.size(); i++) {
            FileInputStream input = null;
            String photoPath = photosPath.get(i);
            try {
                input = new FileInputStream(photoPath);
            } catch (FileNotFoundException e) {
                LogUtils.info("exception");
                e.printStackTrace();
            }
            if (input == null) {
                continue;
            }

            ImageView imageView = settingImageView(input);
            GridPane photoItemGridPane = new GridPane();


            PhotoFileInfo photoFileInfo1 = CacheData.getPhotosFileInfoMap().get(photoPath);
            if (photoFileInfo1 == null) {
                CacheData.setPhotoFileInfo(photoPath);
            }
            PhotoFileInfo photoFileInfo = CacheData.getPhotosFileInfoMap().get(photoPath);
            String name = "";
            if (photoFileInfo != null && GeneParamConfig.isIsNeedPhotoMark()) {
                name = photoFileInfo.getName();
            }

            TextField photoMarkTextField = new TextField(name);
            photoMarkTextField.setAlignment(Pos.BOTTOM_CENTER);
            photoMarkTextField.setStyle("-fx-background-color: transparent");

            photoItemGridPane.add(imageView, 0, 0);
            photoItemGridPane.add(photoMarkTextField, 0, 1);

            if (isPageFoot(row)) {
                int pageMargin = isPageMargin(column);

                int pageNo = getPageNo(i);
                String pageNoStr = pageNo == 0 ? "" : String.valueOf(pageNo);

                Text gap = new Text("    ");
                Text gap2 = new Text(pageMargin != 0 ? "-----" + pageNoStr + "-----" : "    ");
                Text gap3 = new Text("    ");
                VBox box = new VBox(gap, gap2, gap3);

                if (pageMargin == -1 && GeneParamConfig.instance().getColumn() == 1) {
                    // case only one column
                    box.setAlignment(Pos.CENTER);
                } else if (pageMargin == -1) {
                    box.setAlignment(Pos.CENTER_LEFT);
                } else {
                    box.setAlignment(Pos.CENTER_RIGHT);
                }

                photoItemGridPane.add(box, 0, 2);
                GridPane.setValignment(box, VPos.BOTTOM);
            }

            photoItemGridPane.setAlignment(Pos.BOTTOM_CENTER);

            gridPane.add(photoItemGridPane, column++, row);
            if (column == GeneParamConfig.instance().getColumn()) {
                column = 0;
                row++;
            }
        }


        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            // 鼠标开始拖拽图片
            node.setOnMouseDragged(event -> {
                Node source = (Node) event.getSource();
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);
//                System.out.printf("OnMouseDragged: %d, %d%n", rowIndex + 1, colIndex + 1);
                int photoOffset = getPhotoOffset(rowIndex, colIndex);
                String userDragPhoto = CacheData.getPhotosPreviewPath().get(photoOffset);

                GeneParamConfig.setIsUserDragPhoto(true);
                GeneParamConfig.setUserDragPhotoOffset(photoOffset);
                GeneParamConfig.setUserDragPhoto(userDragPhoto);
            });

            // 鼠标进入时
            node.setOnMouseEntered(event -> {
                Node source = (Node) event.getSource();
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);

                // 鼠标进入时显示底色
                if (source instanceof GridPane) {
                    GridPane photoItem = (GridPane) source;
                    photoItem.setStyle("-fx-background-color: rgba(255, 255, 25, 0.5);");
                    if (CacheData.isClick2RemovePhoto) {
                        photoItem.setStyle("-fx-background-color: red;");
                    }
                }


                // 判断是否有拖拽图片到当前位置
                if (GeneParamConfig.isIsUserDragPhoto()) {
                    GridPane photoItem = (GridPane) source;
                    photoItem.setStyle("-fx-background-color: rgba(5, 25, 25, 0.5);");
                    GeneParamConfig.setIsUserDragPhoto(false);
                    // 交换两张照片
                    int mouseOverPhotoOffset = getPhotoOffset(rowIndex, colIndex);
                    CacheData.swapPhoto(GeneParamConfig.getUserDragPhotoOffset(), mouseOverPhotoOffset);

                    GridPane gridPane2 = (GridPane) pane;
                    gridPane2.getChildren().clear();
                    photosViewPane(gridPane2, CacheData.getPhotosPreviewPath());
                }

            });


            // 鼠标退出时不再显示底色
            node.setOnMouseExited(event -> {
                Node source = (Node) event.getSource();
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);
//                System.out.printf("setOnMouseExited: %d, %d%n", rowIndex + 1, colIndex + 1);
                if (source instanceof GridPane) {
                    GridPane photoItem = (GridPane) source;
                    photoItem.setStyle("");
                }
            });


            // 鼠标点击图片时，可能需要删除图片
            node.setOnMouseClicked(event -> {
                Node source = (Node) event.getSource();
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);
//                System.out.printf("setOnMouseClicked: %d, %d%n", rowIndex + 1, colIndex + 1);
                if (CacheData.isClick2RemovePhoto) {
                    long startClick = System.currentTimeMillis();
                    int photoOffset = getPhotoOffset(rowIndex, colIndex);
                    // 移除图片
                    CacheData.removeByOffset(photoOffset);

                    CacheData.refreshGeneConfig("pathShouldRemove");
                    long spend = System.currentTimeMillis() - startClick;
                    LogUtils.info("delete single photo spend(ms): " + spend);
                }
            });


            // 编辑图片下方的文本使用
            GridPane photoItemGridPane = (GridPane) node;
            ObservableList<Node> photoItemChildren = photoItemGridPane.getChildren();
            for (Node photoItemChild : photoItemChildren) {
                if (photoItemChild instanceof TextField) {
                    TextField photoMark = (TextField) photoItemChild;
                    photoMark.textProperty().addListener((observable, oldValue, newValue) -> {
                        GridPane photoViewItem = (GridPane) photoMark.getParent();
                        Integer colIndex = GridPane.getColumnIndex(photoViewItem);
                        Integer rowIndex = GridPane.getRowIndex(photoViewItem);
                        String value = observable.getValue();

                        int photoOffset = getPhotoOffset(rowIndex, colIndex);
                        String photoPath = CacheData.getPhotosPreviewPath().get(photoOffset);
                        PhotoFileInfo photoFileInfo = CacheData.getPhotosFileInfoMap().get(photoPath);
                        photoFileInfo.setMark(value);
                    });
                }
            }
        }

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_CENTER);

        long spend = System.currentTimeMillis() - start;
        LogUtils.info("gridPane spend(ms):" + spend);

        CacheData.setAppStatus(SysConfig.DEFAULT);
        CacheData.refreshStatus();
        return gridPane;
    }


    public GridPane pdfListPane(GridPane pane, List<String> pdfsPath) {
        CacheData.setAppStatus(SysConfig.RENDER_PREVIEW);
        CacheData.refreshStatus();
        GridPane gridPane = pane;
        if (gridPane == null) {
            gridPane = new GridPane();
        }
        if (pdfsPath.size() == 0) {
            // 清除数据后直接返回
            gridPane.getChildren().clear();
            CacheData.setAppStatus(SysConfig.DEFAULT);
            CacheData.refreshStatus();
            return pane;
        }

        int row = 0;
        int column = 0;
        for (int i = 0; i < pdfsPath.size(); i++) {
            String pdfPath = pdfsPath.get(i);
            String fileFullName = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1);
            GridPane pdfItemGridPane = new GridPane();
            Text pdfTextField = new Text(fileFullName);
            pdfItemGridPane.add(pdfTextField, 0, 0);
            gridPane.add(pdfItemGridPane, 0, row);
            row++;
        }


        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            // 鼠标开始拖拽图片
            node.setOnMouseDragged(event -> {
                Node source = (Node) event.getSource();
                // 因为只有一列，直接取 rows
                Integer rowIndex = GridPane.getRowIndex(source);
                String mergePdfPath = CacheData.getMergePdfPath().get(rowIndex);
                GeneParamConfig.setIsUserDragPdf(true);
                GeneParamConfig.setUserDragPdf(mergePdfPath);
            });

            // 鼠标进入时
            node.setOnMouseEntered(event -> {
                Node source = (Node) event.getSource();
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);

                // 鼠标进入时显示底色
                if (source instanceof GridPane) {
                    GridPane photoItem = (GridPane) source;
                    photoItem.setStyle("-fx-background-color: rgba(255, 255, 25, 0.5);");
                    if (CacheData.isClick2RemovePdf) {
                        photoItem.setStyle("-fx-background-color: red;");
                    }
                }


                // 判断是否有拖拽pdf到当前位置
                if (GeneParamConfig.isIsUserDragPdf()) {
                    GridPane photoItem = (GridPane) source;
                    photoItem.setStyle("-fx-background-color: rgba(5, 25, 25, 0.5);");
                    GeneParamConfig.setIsUserDragPdf(false);

                    // 交换pdf
                    String mergePdfPathNow = CacheData.getMergePdfPath().get(rowIndex);
                    String mergePdfPathDrag = GeneParamConfig.getUserDragPdf();
                    ArrayList<String> pdfPathNew = new ArrayList<>();
                    for (String s : CacheData.getMergePdfPath()) {
                        if (s.equalsIgnoreCase(mergePdfPathNow)) {
                            pdfPathNew.add(mergePdfPathDrag);
                        } else if (s.equalsIgnoreCase(mergePdfPathDrag)) {
                            pdfPathNew.add(mergePdfPathNow);
                        } else {
                            pdfPathNew.add(s);
                        }
                    }
                    CacheData.setMergePdfPath(pdfPathNew);

                    GridPane gridPane2 = (GridPane) pane;
                    gridPane2.getChildren().clear();
                    pdfListPane(gridPane2, CacheData.getMergePdfPath());
                }

            });


            // 鼠标退出时不再显示底色
            node.setOnMouseExited(event -> {
                Node source = (Node) event.getSource();
                if (source instanceof GridPane) {
                    GridPane item = (GridPane) source;
                    item.setStyle("");
                }
            });


            // 鼠标点击图片时，可能需要删除图片
            node.setOnMouseClicked(event -> {
                Node source = (Node) event.getSource();
                Integer rowIndex = GridPane.getRowIndex(source);
                if (CacheData.isClick2RemovePdf) {
                    ArrayList<String> pdfPathNew = new ArrayList<>();
                    ArrayList<String> mergePdfPath = CacheData.getMergePdfPath();
                    for (int i = 0; i < mergePdfPath.size(); i++) {
                        if (i == rowIndex) {
                            continue;
                        }
                        pdfPathNew.add(mergePdfPath.get(i));
                    }

                    CacheData.setMergePdfPath(pdfPathNew);

                    // 需要清除已有的pdf, 然后刷新
                    CacheData.gridPane.getChildren().clear();
                    ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
                    viewGridPaneCtrl.pdfListPane(CacheData.gridPane, CacheData.getMergePdfPath());

                    CacheData.refreshStatus();
                }
            });

        }

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_CENTER);

        CacheData.setAppStatus(SysConfig.DEFAULT);
        CacheData.refreshStatus();
        return gridPane;
    }

    private boolean isPageFoot(int row) {
        int rowSet = GeneParamConfig.instance().getRow();
        if ((row + 1) % rowSet == 0) {
            return true;
        }
        return false;
    }

    private int isPageMargin(int column) {
        int columnSet = GeneParamConfig.instance().getColumn();
        if (column == 0) {
            return -1;
        }

        if (column == columnSet - 1) {
            return 1;
        }
        return 0;
    }

    private int getPageNo(int offSet) {
        int columnSet = GeneParamConfig.instance().getColumn();
        int rowSet = GeneParamConfig.instance().getRow();
        int amtPerPage = columnSet * rowSet;

        if ((offSet + 1) % amtPerPage == 0) {
            return (offSet + 1) / amtPerPage;
        }
        return 0;
    }


    private int getPhotoOffset(int rowIndex, int colIndex) {
        int offset = 0;
        if (rowIndex == 0) {
            return colIndex;
        }
        int column = GeneParamConfig.instance().getColumn();
        offset = rowIndex * column + colIndex;
        return offset;
    }

    private ImageView settingImageView(FileInputStream input) {
        ImageView imageView = null;
        int row = GeneParamConfig.instance().getRow();
        int column = GeneParamConfig.instance().getColumn();

        Image image = settingPreviewImage(input);

        if (row == 2 && column == 1) {
            imageView = new ImageView(image);
            imageView.setFitWidth(600);
            imageView.setFitHeight(320);
        }
        if (row == 4 && column == 1) {
            imageView = new ImageView(image);
            imageView.setFitWidth(600);
            imageView.setFitHeight(160);
        }
        if (row == 4 && column == 2) {
            imageView = new ImageView(image);
            imageView.setFitHeight(160);
            imageView.setFitWidth(300);
        }
        if (row == 8 && column == 4) {
            imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(150);
        }
        if (row == 1 && column == 1) {
            imageView = new ImageView(image);
            imageView.setFitWidth(640);
            imageView.setFitHeight(640);
        }
        if (row == 1 && column == 2) {
            imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(640);
        }
        if (imageView != null) {
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);
        }

        return imageView;
    }

    private Image settingPreviewImage(FileInputStream input) {
        int row = GeneParamConfig.instance().getRow();
        int column = GeneParamConfig.instance().getColumn();
        Image image = null;
        double ratio = 1;
        if (row == 2 && column == 1) {
            image = new Image(input);
        }
        if (row == 4 && column == 1) {
            image = new Image(input, 600 * ratio, 160 * ratio, true, true);
        }
        if (row == 4 && column == 2) {
            image = new Image(input, 300 * ratio, 168 * ratio, true, true);
        }
        if (row == 8 && column == 4) {
            image = new Image(input, 150 * ratio, 80 * ratio, true, true);
        }
        if (row == 1 && column == 1) {
            image = new Image(input, 600 * ratio, 600 * ratio, true, true);
        }
        if (row == 1 && column == 2) {
            image = new Image(input, 300 * ratio, 600 * ratio, true, true);
        }

        return image;
    }

    public static FileTime getMostOld(BasicFileAttributes basicFileAttributes) {
        FileTime fileTime = basicFileAttributes.creationTime();
        FileTime fileTime1 = basicFileAttributes.lastModifiedTime();
        FileTime fileTime2 = basicFileAttributes.lastAccessTime();

        if (fileTime.compareTo(fileTime1) > 0) {
            if (fileTime1.compareTo(fileTime2) > 0) {
                return fileTime2;
            } else {
                return fileTime1;
            }
        } else {
            if (fileTime.compareTo(fileTime2) > 0) {
                return fileTime2;
            } else {
                return fileTime;
            }
        }
    }

    public static ArrayList<String> sortPhotos(List<String> photosPath) {
        ArrayList<PhotoFileInfo> photoFileInfos = new ArrayList<>();
        // 1. 收集每一张图片的信息
        for (String path : photosPath) {
            PhotoFileInfo photoFileInfo = getPhotoFileInfo(path);
            photoFileInfos.add(photoFileInfo);
            CacheData.getPhotosFileInfoMap().put(path, photoFileInfo);
        }

        // 2. 按照收集到的信息对图片进行排序
        List<String> res = sortList(photoFileInfos);

        // 3. 返回结果
        return (ArrayList) res;
    }


    public static PhotoFileInfo getPhotoFileInfo(String path) {
        try {
            PhotoFileInfo photoFileInfo1 = CacheData.getPhotosFileInfoMap().get(path);
            if (CacheData.getPhotosFileInfoMap().get(path) != null) {
                return photoFileInfo1;
            }

            BasicFileAttributes basicFileAttributes = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            FileTime fileTime = getMostOld(basicFileAttributes);

            long timestamp = fileTime.toMillis();
            LocalDateTime createTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
            String fileFullPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            PhotoFileInfo photoFileInfo = new PhotoFileInfo();
            photoFileInfo.setAbsolutePath(path);
            photoFileInfo.setFormat(fileFormat);
            photoFileInfo.setName(fileName);
            photoFileInfo.setCreateTime(createTime);

            // 如果图片是在压缩路径中的情况，找回该压缩图片对应的原始图片的创建信息
            if (fileFullPath.endsWith("photo2pdf" + File.separator + "previewPhotos" + File.separator)) {
                String originalPhoto = CacheData.getCompressPhoto2OriginalPhotoMap().get(path);
                if (originalPhoto != null) {
                    FileTime fileTimeOriginal = Files.readAttributes(Paths.get(originalPhoto), BasicFileAttributes.class).creationTime();
                    long timestampOriginal = fileTimeOriginal.toMillis();
                    LocalDateTime createTimeOriginal = Instant.ofEpochMilli(timestampOriginal).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                    photoFileInfo.setCreateTime(createTimeOriginal);
                }

            }
            return photoFileInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<String> sortList(List<PhotoFileInfo> photoFileInfos) {
        List<String> res = null;
        if ("Date".equalsIgnoreCase(GeneParamConfig.getSortPhotosBy()) && "DESC".equalsIgnoreCase(GeneParamConfig.getSortPhotosOrderBy())) {
            res = photoFileInfos.stream()
                    .sorted(Comparator.comparing(PhotoFileInfo::getCreateTime).reversed())
                    .map(PhotoFileInfo::getAbsolutePath).collect(Collectors.toList());
        } else if ("Date".equalsIgnoreCase(GeneParamConfig.getSortPhotosBy()) && "ASC".equalsIgnoreCase(GeneParamConfig.getSortPhotosOrderBy())) {
            res = photoFileInfos.stream()
                    .sorted(Comparator.comparing(PhotoFileInfo::getCreateTime))
                    .map(PhotoFileInfo::getAbsolutePath).collect(Collectors.toList());
        } else if ("Name".equalsIgnoreCase(GeneParamConfig.getSortPhotosBy()) && "DESC".equalsIgnoreCase(GeneParamConfig.getSortPhotosOrderBy())) {
            res = photoFileInfos.stream()
                    .sorted(Comparator.comparing(PhotoFileInfo::getName).reversed())
                    .map(PhotoFileInfo::getAbsolutePath).collect(Collectors.toList());
        } else if ("Name".equalsIgnoreCase(GeneParamConfig.getSortPhotosBy()) && "ASC".equalsIgnoreCase(GeneParamConfig.getSortPhotosOrderBy())) {
            res = photoFileInfos.stream()
                    .sorted(Comparator.comparing(PhotoFileInfo::getName))
                    .map(PhotoFileInfo::getAbsolutePath).collect(Collectors.toList());
        } else {
            res = CacheData.getPhotosPathUserSelectOrder();
        }

        return res;
    }

}
