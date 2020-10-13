#### lab1

Q：

~~fileManager和blockmanager分组的数量是写死的还是要动态变化？动态变化的话，是否提供给用户增加、删除分组的接口？~~

~~文件管理系统如何接收输入，从控制台接收用户输入还是什么？~~

什么情况下会导致写入失败，貌似很难发生这种情况？还是说要在代码里刻意设置一种情况，条件满足时，只写入一半文件就停止？

smart-cat是以正常形式输出吗（不需要转换成16进制）？

因为有多副本操作，所以是不是需要读取blockMeta

1、Id的作用是什么？是类似一个long类型的数字，传进去给文件、块编号用的吗?

2、smart-hex(Block block) 这个函数传的应该是块号而不是block对象吧？

3、smart-write(String fm,String fileName, int index) 参数似乎少了要写入的内容 String content

4、块储存的数据大小可以是不一样的吗？比如说一个文件有四个块，块数据大小分别为32KB，20KB，32KB，6KB

~~用户可以选择文件所在的fileManager吗？~~

【lab增加参数】
因为有不少同学问，所以最后工具参考实现加上一个统一的参数吧
smart-cat( fm，String fileName)
smart-hex(Block block)
smart-write(fm,String fileName, int index)
smart-copy(fm,String from, String to)

write进去的具体内容通过console输入；write实现的是插入而不是覆盖

【关于Id】
具体的实现按照自己的需要来设计就好了，合理且能够完成功能即可

【关于三个位置move】
这三个是用来传进move函数的where参数的，所以不需要修改

【smart-cat】
这个函数的参数再加一个fm吧～

相关的smart-write和smart-copy同理



file：

size blocksize

block0

block1

......

blockn

```
/**
 * 记录三个块管理器中的最大文件数量
 */
```



#### 收获

该对象流会在文件不存在时自动创建该文件

```java
ObjectOutputStream objectOutputStream=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))
```





















