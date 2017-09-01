package code.parser.core.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * Velocity工具类
 * 
 * @author dinghn
 *
 */
public final class VelocityUtil {

	private static VelocityEngine ve;

	private VelocityUtil() {
		super();
	}

	public static String getContentForTemplatePath(Reader reader,
			Map<?, ?> contextMap)
			throws Exception {
		VelocityContext context = new VelocityContext();
		Iterator<?> it = contextMap.entrySet().iterator();
		Entry<?, ?> entry = null;
		while (it.hasNext()) {
			entry = (Entry<?, ?>) it.next();
			context.put((String) entry.getKey(), entry.getValue());
		}
		StringWriter writer = new StringWriter();
		init();
		ve.evaluate(context, writer, "", reader);

		String result = writer.toString().replaceAll("\\$ .", "\\$.");
		if (result.lastIndexOf("\r\n") > 0
				&& result.lastIndexOf("\r\n") + 2 == result.length()) {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}

	public static String getContentForTemplatePath(String path, Map<?, ?> contextMap)
			throws Exception {
		try (InputStream is = VelocityUtil.class
				.getResourceAsStream(path);
				Reader reader = new InputStreamReader(is)) {
			return getContentForTemplatePath(reader, contextMap);
		}
	}

	private static VelocityEngine init() throws Exception {
		if (VelocityUtil.ve == null) {
			VelocityUtil.ve = new VelocityEngine();
			Properties props = new Properties();

			props.setProperty("resource.loader", "class");
			props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			props.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			
			
			// fix bug for
			// org.apache.velocity.exception.VelocityException: The specified
			// class for ResourceManager
			// (org.apache.velocity.runtime.resource.ResourceManagerImpl) does
			// not implement
			// org.apache.velocity.runtime.resource.ResourceManager; Velocity is
			// not initialized correctly.

			// VelocityUtil.ve.init(props);
			Thread thread = Thread.currentThread();
			ClassLoader loader = thread.getContextClassLoader();
			thread.setContextClassLoader(VelocityUtil.class.getClassLoader());
			try {
				// ... Velocity library call(s) ...
				VelocityUtil.ve.init(props);
			} finally {
				thread.setContextClassLoader(loader);
			}
		}
		return VelocityUtil.ve;
	}
}