package implement;

import org.junit.jupiter.api.Assertions;

class FileImplTest
{

    @org.junit.jupiter.api.Test
    void read()
    {
        String fm="FM1";
        String filename="f1";
        FileManagerImpl fileManager=FileManagerImpl.getFileManager();
        FileImpl file=fileManager.getFileImpl(fm,filename);
        System.out.println("文件当前光标为"+file.getCursor()+" 文件内容为：");
        System.out.println(new String(file.getFileData()));
        file.setCursor(0);

        System.out.println("读取11个长度");
        System.out.println(new String(file.read(11)));
        System.out.println("文件当前光标为"+file.getCursor());
        System.out.println("读取1个长度");
        file.move(3,2);
        String res =new String(file.read(2));
        System.out.println(res);

    }

    @org.junit.jupiter.api.Test
    void write()
    {
    }

    @org.junit.jupiter.api.Test
    void move()
    {
    }

    @org.junit.jupiter.api.Test
    void setSize()
    {
        FileManagerImpl fileManager=FileManagerImpl.getFileManager();
        String fm="FM1";
        String filename="f1";
        FileImpl file=fileManager.getFileImpl(fm,filename);
        System.out.println(file.size());
        System.out.println("原文件内容");
        System.out.println(new String(file.getFileData()));
        //扩大10个byte大小
        int oldSize=file.getFileSize()+10;
        file.setSize(oldSize);
        Assertions.assertEquals(file.getFileSize(),oldSize);
        System.out.println("扩大10个byte大小");
        System.out.println(new String(file.getFileData()));
        //扩大40个byte大小
        int oldSize2=(int)file.size()+40;
        file.setSize(oldSize2);
        Assertions.assertEquals(file.getFileSize(),oldSize2);
        System.out.println("扩大40个byte大小");
        System.out.println(new String(file.getFileData()));
        //缩小20个byte大小
        int oldSize3=file.getFileSize()-20;
        file.setSize(oldSize3);
        Assertions.assertEquals(file.getFileSize(),oldSize3);
        System.out.println("缩小20个byte大小");
        System.out.println(new String(file.getFileData()));
        //再缩小40个byte大小
        int oldSize4=file.getFileSize()-40;
        file.setSize(oldSize4);
        Assertions.assertEquals(file.getFileSize(),oldSize4);
        System.out.println("缩小40个byte大小");
        System.out.println(new String(file.getFileData()));
    }

    @org.junit.jupiter.api.Test
    void insert()
    {
    }
}