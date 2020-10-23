package my_interface;

public interface Block
{
    Id getIndexId();
    BlockManager getBlockManager();
    byte[] read();
    int blockSize();
}
