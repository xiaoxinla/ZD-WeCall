<?php
require_once("./rsa.php");

$mysql = new SaeMysql();
$sql = "INSERT INTO user (aid,name,phone) VALUES ('','','')";
$mysql->runSql($sql);
$sql = "SELECT MAX(did) FROM user";
$result = $mysql->getVar($sql);
echo RSA::privEncrypt($result);