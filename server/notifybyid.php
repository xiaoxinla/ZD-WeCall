<?php
require_once("./aes.php");
require_once("./response.php");
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
$did = $json->did;
$name = $json->name;
$phone = $json->phone;
$sql = "SELECT aid FROM user WHERE did=$did";
$token = $mysql->getVar($sql);
$dkey = adjustKey($token);
$aes->set_key($dkey);
$aes->require_pkcs5();
$arr = array(
	'name' => $name,
	'phone' =>$phone
	);
// $data = $aes->encrypt(json_encode($arr));
// $rst = array(
// 	'data'=>$data
// 	);

$appid = 22633;
$title = '联系人添加请求';
$msg = $name.'请求您添加联系人';
$acts = "[\"2,com.wecall.contacts,com.wecall.contacts.ContactEditor\"]";
$extra = array(
    'handle_by_app'=>'0',
    'type' => 1,
    'name' => $name,
    'phone' => $phone
);
$adpns = new SaeADPNS();
$result = $adpns->push($appid, $token, $title, $msg, $acts, $extra);
if ($result && is_array($result)) {
    echo '发送成功！';
    var_dump($result);
} else {
    echo '发送失败。';
    var_dump($apns->errno(), $apns->errmsg());
}
