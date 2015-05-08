<?php
require_once("./rsa.php");
require_once("./response.php");

$did = Rsa::privDecrypt($_POST['did']);
$aid = Rsa::privDecrypt($_POST['aid']);
$name = Rsa::privDecrypt($_POST['name']);
$phone = Rsa::privDecrypt($_POST['phone']); 

$sql = "UPDATE user set aid='$aid',name='$name',phone='$phone' WHERE did=$did";
$mysql = new SaeMysql();
$result = $mysql->runSql($sql);
if($result){
	echo Rsa::privEncrypt("success");
}else{
	echo Rsa::privEncrypt("failed");
}
