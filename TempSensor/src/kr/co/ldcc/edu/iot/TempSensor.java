package kr.co.ldcc.edu.iot;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import comus.wp.onem2m.iwf.common.M2MException;
import comus.wp.onem2m.iwf.nch.NotifyResponse;
import comus.wp.onem2m.iwf.run.CmdListener;
import comus.wp.onem2m.iwf.run.IWF;

public class TempSensor {

	private static final Logger LOG = LoggerFactory.getLogger(TempSensor.class);

	private int pin = 2;
	private String OID = "0004000100010001_12345671";
	private DHT11 sensor = new DHT11();
	private String on = "1";
	private String off = "0";
	
	private Runtime rt;
	private String req;
	private IWF vDevice;
	private Float temp;
	
	private void register() throws Exception {
		try {
			vDevice = new IWF(OID);
		} catch (IOException | M2MException e) {
			e.printStackTrace();
			throw new Exception(">> 선언 실패");
		}
		vDevice.register();
	}
	
	private void listen() {
		if(vDevice != null) {
			rt = Runtime.getRuntime();
			try {
				rt.exec("gpio mode 0 out");
			} catch (IOException e) {
				e.printStackTrace();
			}
			vDevice.addCmdListener(new CmdListener() {

				@Override
				public void excute(Map<String, String> cmd, NotifyResponse resp) {
					rt = Runtime.getRuntime();
					if( (req = cmd.get("switch")) != null) {
						if(on.equals(req)) {
							// 전구 켜
							try {
								rt.exec("gpio write 0 1");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else if(off.equals(req)) {
							// 전구 꺼
							try {
								rt.exec("gpio write 0 0");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else {
							System.out.println("잘못된 데이터");
						}
						
						vDevice.putContent("controller-switch", "text/plain", "" + req);
					}
					else {
						System.out.println("데이터 없음");
					}
				}
				
			});
		}
		System.out.println(">>등록이 안됨");
	}

	private void check() throws Exception {
		if (vDevice != null) {
			while (true) {
				if ((temp = sensor.getTemperature(pin)) != null)
					vDevice.putContent("temperature", "text/plain", "" + temp);
				else {
					LOG.warn(">> 패리티 체크 오류 발생");
					continue;
				}
				Thread.sleep(10000);
			}
		} else {
			throw new Exception(">> 등록이 되어 있지 않음");
		}
	}

	public static void main(String[] args) throws Exception {

		TempSensor sensor = new TempSensor();

		sensor.register();
		sensor.listen();
		sensor.check();

	}
}