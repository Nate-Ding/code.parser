/**
 * 
 */
package code.parser.core.convertor;


/**
 * 转换器基类
 * 
 * @author dinghn
 *
 */
public interface IConvertor {
	public byte[] convert(String content) throws Exception;
}
