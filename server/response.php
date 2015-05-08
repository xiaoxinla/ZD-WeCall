<?php

class Response{

	/**
	*按Json方式输出数据
	*/
	public static function genJson($state,$data = array()){
		if(!is_numeric($state)){
			return '';
		}

		$result = array(
			'state' => $state,
			'data' => $data
			);
		echo json_encode($result);
		exit;
	}
}
