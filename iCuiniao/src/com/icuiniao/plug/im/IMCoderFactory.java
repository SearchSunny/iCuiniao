/**
 * 
 */
package com.icuiniao.plug.im;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author hw
 *
 */
public class IMCoderFactory implements ProtocolCodecFactory{

	private IMEncoder encoder;
	private IMDecoder decoder;
	
	public IMCoderFactory(){
		encoder = new IMEncoder();
		decoder = new IMDecoder();
	}
	
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}

}
