package com.suryoday.FdOpening.Others;

import java.io.InputStream;
import java.util.Properties;

public class CustomProperties {

	private String otpUrl;
	private String kycotpurl;
	private String kycUrl;
	private String publicKeyFile;
	private String pidOptsVer;
	private String fingerPrintCount;
	private String fingerPrintType;
	private String irirsCount;
	private String irisType;
	private String AuthPreProdCert;
	private String passFileNo;
	private String passDob;
	private String passName;

	public String getKycotpurl() {
		return kycotpurl;
	}

	public void setKycotpurl(String kycotpurl) {
		this.kycotpurl = kycotpurl;
	}

	public void setOtpUrl(String otpUrl) {
		this.otpUrl = otpUrl;
	}

	public void setKycUrl(String kycUrl) {
		this.kycUrl = kycUrl;
	}

	public void setPublicKeyFile(String publicKeyFile) {
		this.publicKeyFile = publicKeyFile;
	}

	public void setPidOptsVer(String pidOptsVer) {
		this.pidOptsVer = pidOptsVer;
	}

	public void setFingerPrintCount(String fingerPrintCount) {
		this.fingerPrintCount = fingerPrintCount;
	}

	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

	public void setIrirsCount(String irirsCount) {
		this.irirsCount = irirsCount;
	}

	public void setIrisType(String irisType) {
		this.irisType = irisType;
	}

	public void setPidFormat(String pidFormat) {
		this.pidFormat = pidFormat;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setPosh(String posh) {
		this.posh = posh;
	}

	public void setAuthVer(String authVer) {
		this.authVer = authVer;
	}

	public void setOtpChannel(String otpChannel) {
		this.otpChannel = otpChannel;
	}

	public void setKycVer(String kycVer) {
		this.kycVer = kycVer;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public void setUseSSK(String useSSK) {
		this.useSSK = useSSK;
	}

	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public void setLov(String lov) {
		this.lov = lov;
	}

	public void setIrisLicenseKey(String irisLicenseKey) {
		this.irisLicenseKey = irisLicenseKey;
	}

	public void setWadhVer(String wadhVer) {
		this.wadhVer = wadhVer;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	
	public String getPassFileNo() {
		return passFileNo;
	}

	public void setPassFileNo(String passFileNo) {
		this.passFileNo = passFileNo;
	}

	public String getPassDob() {
		return passDob;
	}

	public void setPassDob(String passDob) {
		this.passDob = passDob;
	}

	public String getPassName() {
		return passName;
	}

	public void setPassName(String passName) {
		this.passName = passName;
	}


	private String pidFormat;
	private String timeout;
	private String posh;
	private String authVer;
	private String otpChannel;
	private String kycVer;
	private String env;
	private String useSSK;
	private String ac, sa;
	private String IIN, Mcc, acqId;
	private String tId, rc,tidOtp;
	private String ki, pos_entry_mode, pos_code;
	private String cA_Tid, cA_ID, cA_TA;
	private String aua_lk, kua_lk,opt_lk;
	private String publicIP, lot, lov;
	private String irisLicenseKey;
	private String wadhVer;
	private String authUrl;
	private String esbUrl;
	private String urlVersion;
	private String proc_code, ver;
	private String pi, pa, pfa, bio, bt, pin, otp;
	private String ra, pfr, lr, de;
	private String cibilUrl;
	private String ekyc_key;
	

	/*
	 * Auth 2.0 / KYC 2.1 compatible properties
	 */
	public CustomProperties() throws Exception {
		// ujjivan properties for uat uidai_admin_properties.
		// String filePath = "/../prop/uidai_admin_iris.properties";

		// String filePath =
		// this.getClass().getClassLoader().getResource("props/uidai_admin_iris.properties").toExternalForm();

		String sConfigFile = "props/uidai_admin_iris.properties";

		InputStream in = CustomProperties.class.getClassLoader().getResourceAsStream(sConfigFile);
		if (in == null) {
			// File not found! (Manage the problem)
		}
		Properties prop = new Properties();
		prop.load(in);

//		String filePath = getClass().getClassLoader().getResource("props/uidai_admin_iris.properties").getPath();
//		Properties prop = new Properties();
//		prop.load(new FileInputStream(new File(filePath)));

		otpUrl = prop.getProperty("otpUrl");
		kycotpurl = prop.getProperty("authOtpUrl");
		kycUrl = prop.getProperty("kycUrl");
		publicKeyFile = prop.getProperty("publicKeyFile");
		pidOptsVer = prop.getProperty("optionsver");
		fingerPrintCount = prop.getProperty("fCount");
		fingerPrintType = prop.getProperty("fType");
		irirsCount = prop.getProperty("iCount");
		irisType = prop.getProperty("iType");
		pidFormat = prop.getProperty("format");
		timeout = prop.getProperty("timeout");
		posh = prop.getProperty("posh");
		authVer = prop.getProperty("authVer");
		otpChannel = prop.getProperty("otpchannel");
		kycVer = prop.getProperty("kycVer");
		env = prop.getProperty("env");
		publicIP = prop.getProperty("publicIP");
		lot = prop.getProperty("lot");
		lov = prop.getProperty("lov");
		useSSK = prop.getProperty("useSSK");
		irisLicenseKey = prop.getProperty("irisLicenseKey");
		ac = prop.getProperty("ac");
		sa = prop.getProperty("sa");
		IIN = prop.getProperty("IIN");
		Mcc = prop.getProperty("Mcc");
		acqId = prop.getProperty("AcqId");
		tId = prop.getProperty("tid");
		tidOtp = prop.getProperty("tidOtp");
		rc = prop.getProperty("rc");
		ki = prop.getProperty("ki");
		pos_entry_mode = prop.getProperty("pos_entry_mode");
		pos_code = prop.getProperty("pos_code");
		cA_Tid = prop.getProperty("cA_Tid");
		cA_ID = prop.getProperty("cA_ID");
		cA_TA = prop.getProperty("cA_TA");
		aua_lk = prop.getProperty("aua_lk");
		kua_lk = prop.getProperty("kua_lk");
		opt_lk = prop.getProperty("opt_lk");
		wadhVer = prop.getProperty("wadhVer");
		authUrl = prop.getProperty("authUrl");
		esbUrl = prop.getProperty("esbUrl");
		urlVersion = prop.getProperty("urlVersion");
		proc_code = prop.getProperty("proc_code");
		ver = prop.getProperty("ver");
		pi = prop.getProperty("pi");
		pa = prop.getProperty("pa");
		pfa = prop.getProperty("pfa");
		bio = prop.getProperty("bio");
		bt = prop.getProperty("bt");
		pin = prop.getProperty("pin");
		otp = prop.getProperty("otp");
		ra = prop.getProperty("ra");
		pfr = prop.getProperty("pfr");
		lr = prop.getProperty("lr");
		de = prop.getProperty("de");
		cibilUrl = prop.getProperty("cibilUrl");
		ekyc_key = prop.getProperty("ekyc_key");
		AuthPreProdCert=prop.getProperty("AuthPreProdCert");

	}

	public String getTidOtp() {
		return tidOtp;
	}

	public void setTidOtp(String tidOtp) {
		this.tidOtp = tidOtp;
	}

	public String getOpt_lk() {
		return opt_lk;
	}

	public void setOpt_lk(String opt_lk) {
		this.opt_lk = opt_lk;
	}

	public String getOtpUrl() {
		return otpUrl;
	}

	public String getKycUrl() {
		return kycUrl;
	}

	public String getPublicKeyFile() {
		return publicKeyFile;
	}

	public String getPidOptsVer() {
		return pidOptsVer;
	}

	public String getFingerPrintCount() {
		return fingerPrintCount;
	}

	public String getFingerPrintType() {
		return fingerPrintType;
	}

	public String getIrirsCount() {
		return irirsCount;
	}

	public String getIrisType() {
		return irisType;
	}

	public String getPidFormat() {
		return pidFormat;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getPosh() {
		return posh;
	}

	public String getAuthVer() {
		return authVer;
	}

	public String getOtpChannel() {
		return otpChannel;
	}

	public String getKycVer() {
		return kycVer;
	}

	public String getEnv() {
		return env;
	}

	public String getPublicIP() {
		return publicIP;
	}

	public String getLot() {
		return lot;
	}

	public String getLov() {
		return lov;
	}

	public String getUseSSK() {
		return useSSK;
	}

	public String getIrisLicenseKey() {
		return irisLicenseKey;
	}

	public String getAc() {
		return ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getSa() {
		return sa;
	}

	public void setSa(String sa) {
		this.sa = sa;
	}

	public String getIIN() {
		return IIN;
	}

	public void setIIN(String iIN) {
		IIN = iIN;
	}

	public String getMcc() {
		return Mcc;
	}

	public void setMcc(String mcc) {
		Mcc = mcc;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getKi() {
		return ki;
	}

	public void setKi(String ki) {
		this.ki = ki;
	}

	public String getPos_entry_mode() {
		return pos_entry_mode;
	}

	public void setPos_entry_mode(String pos_entry_mode) {
		this.pos_entry_mode = pos_entry_mode;
	}

	public String getPos_code() {
		return pos_code;
	}

	public void setPos_code(String pos_code) {
		this.pos_code = pos_code;
	}

	public String getcA_Tid() {
		return cA_Tid;
	}

	public void setcA_Tid(String cA_Tid) {
		this.cA_Tid = cA_Tid;
	}

	public String getcA_ID() {
		return cA_ID;
	}

	public void setcA_ID(String cA_ID) {
		this.cA_ID = cA_ID;
	}

	public String getcA_TA() {
		return cA_TA;
	}

	public void setcA_TA(String cA_TA) {
		this.cA_TA = cA_TA;
	}

	public String getAua_lk() {
		return aua_lk;
	}

	public void setAua_lk(String aua_lk) {
		this.aua_lk = aua_lk;
	}

	public String getKua_lk() {
		return kua_lk;
	}

	public void setKua_lk(String kua_lk) {
		this.kua_lk = kua_lk;
	}

	public String getWadhVer() {
		return wadhVer;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public String getEsbUrl() {
		return esbUrl;
	}

	public void setEsbUrl(String esbUrl) {
		this.esbUrl = esbUrl;
	}

	public String getUrlVersion() {
		return urlVersion;
	}

	public void setUrlVersion(String urlVersion) {
		this.urlVersion = urlVersion;
	}

	public String getProc_code() {
		return proc_code;
	}

	public void setProc_code(String proc_code) {
		this.proc_code = proc_code;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPfa() {
		return pfa;
	}

	public void setPfa(String pfa) {
		this.pfa = pfa;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getBt() {
		return bt;
	}

	public void setBt(String bt) {
		this.bt = bt;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public String getPfr() {
		return pfr;
	}

	public void setPfr(String pfr) {
		this.pfr = pfr;
	}

	public String getLr() {
		return lr;
	}

	public void setLr(String lr) {
		this.lr = lr;
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getCibilUrl() {
		return cibilUrl;
	}

	public void setCibilUrl(String cibilUrl) {
		this.cibilUrl = cibilUrl;
	}

	public String getEkyc_key() {
		return ekyc_key;
	}

	public void setEkyc_key(String ekyc_key) {
		this.ekyc_key = ekyc_key;
	}

	public String getAuthPreProdCert() {
		return AuthPreProdCert;
	}

	public void setAuthPreProdCert(String authPreProdCert) {
		AuthPreProdCert = authPreProdCert;
	}
	
	

}