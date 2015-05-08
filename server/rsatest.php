<?php

require_once('./rsa.php');

$encrypt = $_POST['data'];
$data = Rsa::privDecrypt($encrypt);
echo Rsa::privEncrypt($data);