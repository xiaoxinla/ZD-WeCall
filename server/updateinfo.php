<?php
require_once("./aes.php");
function adjustKey($key){
	if(strlen($key)<16){
		$key = $key.'0000000000000000';
	}
	if(strlen($key)>16){
		$key = substr($key, 0, 16);
	}
	return $key;
}

$mysql = new SaeMysql();
$did = $_POST['did'];
$data = $_POST['data'];
$sql = "SELECT aid FROM user WHERE did=$did";
$skey = $mysql->getVar($sql);
$skey = adjustKey($skey);
$aes = new AES();
$aes->set_key($skey);
$aes->require_pkcs5();
$json = json_decode($aes->decrypt($data));
$name = $json->name;
$phone = $json->phone;
$sql = "UPDATE user SET name='$name',phone='$phone' WHERE did=$did";
$mysql->runSql($sql);