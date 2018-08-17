package com.lu.face.faceenginedemo.engine.models;


import com.hisign.AS60xSDK.entity.MatchResult;

public class FpMatchResult {
	private MatchResult matchResult;
	private FpCollectRet fpCollectRet;

	public MatchResult getMatchResult() {
		return matchResult;
	}

	public void setMatchResult(MatchResult matchResult) {
		this.matchResult = matchResult;
	}

	public FpCollectRet getFpCollectRet() {
		return fpCollectRet;
	}

	public void setFpCollectRet(FpCollectRet fpCollectRet) {
		this.fpCollectRet = fpCollectRet;
	}
}
