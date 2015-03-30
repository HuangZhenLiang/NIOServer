import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.net.ServerSocket;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.ServerSocketChannel;  
import java.nio.channels.SocketChannel;  
import java.util.Iterator;  
import java.util.Set;  
  
public class NIOServer {  
      public static void main(String[] args) throws IOException{
    	  ServerSocketChannel ssc=ServerSocketChannel.open();
    	  ServerSocket ss=ssc.socket();
    	  Selector s=Selector.open();
    	  ss.bind(new InetSocketAddress(1234));
    	  ssc.configureBlocking(false);
    	  ssc.register(s, SelectionKey.OP_ACCEPT);
    	  while(true){
    		  int n=s.select();
    		  if(n==0)
    			  continue;
    		  Iterator i = s.selectedKeys().iterator();
    		  while(i.hasNext()){
    			  SelectionKey key=(SelectionKey)i.next();
    			  if(key.isAcceptable()){
    				  System.out.println("accept request...");
    				  ServerSocketChannel sc=(ServerSocketChannel)key.channel();
    				  
    				  SocketChannel tmpSC=sc.accept();
    				  tmpSC.configureBlocking(false);
    				  tmpSC.register(s, SelectionKey.OP_READ);
    			  }
    			  if(key.isReadable()){
    				  SocketChannel sc=(SocketChannel)key.channel();
    				  ByteBuffer buf=ByteBuffer.allocate(1024);
    				  Integer count=(Integer)key.attachment();
    				  if(count!=null){
    					  System.out.println("read count:"+count);
    					  count=count+1;
    				  }
    				  else{
    					  System.out.println("read count:0");
    					  count=1;
    				  }
    				  key.attach(count);
    				  int len=0;
    				  while((len=sc.read(buf))>0){
    					  buf.flip();
    					  System.out.println(new String(buf.array()));
    					  buf.clear();
    				  }
    				  if(len<0)
    				  {
    					  sc.close();
    					  System.out.println("close connection");
    				  }
    				  
    			  }
    			  i.remove();
    		  }
    	  }
      }
}