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
$did = $_POST['did'];
$data = $_POST['data'];
$mysql = new SaeMysql();
$sql = "SELECT aid FROM user WHERE did=$did";
$skey = $mysql->getVar($sql);
$skey = adjustKey($skey);
$aes = new AES();
$aes->set_key($skey);
$aes->require_pkcs5();
$info = $aes->decrypt($data);
// echo $info;
$sql = "INSERT INTO feedback (did,info) VALUES ($did,'$info')";
$mysql->runSql($sql);
echo success;