package kr.co.ldcc.edu.iot;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import comus.wp.onem2m.iwf.common.M2MException;
import comus.wp.onem2m.iwf.run.IWF;

public class TempSensor {

	private static final Logger LOG = LoggerFactory.getLogger(TempSensor.class);

	private IWF vDevice;
	private Float temp;

	private int pin = 2;
	private String OID = "0004000100010001_12345671";
	private DHT11 sensor = new DHT11();

	private void register() throws Exception {
		try {
			vDevice = new IWF(OID);
		} catch (IOException | M2MException e) {
			e.printStackTrace();
			throw new Exception(">> 선언 실패");
		}
		vDevice.register();
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
		sensor.check();

	}
}