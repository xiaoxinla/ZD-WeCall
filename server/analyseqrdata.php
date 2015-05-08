<?php
header("Content-Type: text/html;charset=utf-8"); 

require_once("./response.php");
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
$skey = '';
//解密第一层数据
$did = $_POST['did'];
$data = $_POST['data'];
$mysql = new SaeMysql();
if($did!=-1){
	$sql = "SELECT aid FROM user WHERE did=$did";
	$skey = $mysql->getVar($sql);
}else{
	$skey = 'wecall';
}
$skey = adjustKey($skey);
// echo $skey;
$aes = new AES();
$aes->set_key($skey);
$aes->require_pkcs5();
$json = json_decode($aes->decrypt($data));
// echo $aes->decrypt($data);
//解密第二层数据
$ddid = $json->did;
$data = $json->data;
$sql = "SELECT aid FROM user WHERE did=$ddid";
// echo $ddid;
$dkey = $mysql->getVar($sql);
$dkey = adjustKey($dkey);
$aes->set_key($dkey);
$aes->require_pkcs5();
$json = json_decode($aes->decrypt($data));
$name = $json->name;
$phone = $json->phone;

$sql = "SELECT did FROM user WHERE phone='$phone' ORDER BY did desc";
$ddid = $mysql->getVar($sql);
// echo $ddid;
if(!$ddid){
	$ddid = (int)-1;
}
// echo $ddid;
//返回json数据
$arr = array(
	'did' => $ddid,
	'name' => $name,
	'phone' => $phone
	);
$aes->set_key($skey);
$aes->require_pkcs5();
$result = $aes->encrypt(json_encode($arr));
$data = array(
	'data'=>$result
	);
Response::genJson(200,$data);