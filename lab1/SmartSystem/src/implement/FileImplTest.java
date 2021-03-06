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
        //打印完一遍内容后，将光标重置为0
        file.setCursor(0);
        System.out.println("开始读取指定长度");
        file.move(2,1);
        String res =new String(file.read(0));
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

        //缩小12个byte大小
        int oldSize3=file.getFileSize()-12;
        file.setSize(oldSize3);
        Assertions.assertEquals(file.getFileSize(),oldSize3);
        System.out.println("缩小12个byte大小");
        System.out.println(new String(file.getFileData()));

    }

    @org.junit.jupiter.api.Test
    void close()
    {
        FileManagerImpl fileManager=FileManagerImpl.getFileManager();
        FileImpl file1=fileManager.getFileImpl("FM1","f1");
        FileImpl file2=fileManager.getFileImpl("FM1","f2");
        file1.getFileData();
        file2.getFileData();
        file1.close();
        file2.close();
    }
}