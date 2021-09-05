/*
    ScadaBR - http://scadabr.com.br
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.serotonin.mango.rt.dataSource.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.serotonin.modbus4j.serial.SerialPortWrapper;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/**
 *
 * 
 * This class is not finished
 * 
 * @author Terry Packer
 * 
 */

public class SerialPortWrapperImpl implements SerialPortWrapper {
	private SerialPort serialPort;
	private String commPortId;
	private int baudRate;
	private int flowControlIn;
	private int flowControlOut;
	private int dataBits;
	private int stopBits;
	private int parity;
	private int timeOutComPort;

	public SerialPortWrapperImpl() {
		super();
	}

	public SerialPortWrapperImpl(String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits,
			int stopBits, int parity, int timeOutComPort) {
		this.commPortId = commPortId;
		this.baudRate = baudRate;
		this.flowControlIn = flowControlIn;
		this.flowControlOut = flowControlOut;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.timeOutComPort = timeOutComPort;
	}

	public String getCommPortId() {
		return commPortId;
	}

	public void setCommPortId(String commPortId) {
		this.commPortId = commPortId;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public void setFlowControlIn(int flowControlIn) {
		this.flowControlIn = flowControlIn;
	}

	public void setFlowControlOut(int flowControlOut) {
		this.flowControlOut = flowControlOut;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	@Override
	public void close() throws Exception {
		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
		}
	}

	@Override
	public void open() throws Exception {
		// System.out.println("Opening serial port " + commPortId);
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(commPortId);
		CommPort commPort;

		try {
			commPort = portIdentifier.open(commPortId, timeOutComPort);
		} catch (Exception e) {
			System.out.println("SerialPortWrapperImpl: error opening serial port " + commPortId);
			e.printStackTrace();
			// Rethrow
			throw e;
		}

		if (commPort != null && commPort instanceof SerialPort) {
			serialPort = (SerialPort) commPort;
			try {
				serialPort.setSerialPortParams(this.getBaudRate(), this.getDataBits(), this.getStopBits(),
						this.getParity());
				serialPort.setFlowControlMode(this.getFlowControlIn() | this.getFlowControlOut());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}
		} else {
			// System.out.println("Oops!");
		}
	}

	@Override
	public InputStream getInputStream() {
		InputStream in = null;
		try {
			in = serialPort.getInputStream();
		} catch (IOException e) {
			System.out.println("SerialPortWrapperImpl: error getting input stream: " + e);
			e.printStackTrace();
		}
		return in;
	}

	@Override
	public OutputStream getOutputStream() {
		OutputStream out = null;
		try {
			out = serialPort.getOutputStream();
		} catch (IOException e) {
			System.out.println("SerialPortWrapperImpl: error getting output stream: " + e);
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public int getBaudRate() {
		return this.baudRate;
	}

	@Override
	public int getStopBits() {
		return this.stopBits;
	}

	@Override
	public int getParity() {
		return this.parity;
	}

	@Override
	public int getFlowControlIn() {
		return this.flowControlIn;
	}

	@Override
	public int getFlowControlOut() {
		return this.flowControlOut;
	}

	@Override
	public int getDataBits() {
		return this.dataBits;
	}
}
