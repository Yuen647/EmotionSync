const startBtn = document.getElementById('start-btn');
const resultDiv = document.getElementById('result');

const recognition = new webkitSpeechRecognition();
recognition.lang = 'zh-CN'; // 设置语音识别的语言为中文

startBtn.addEventListener('click', () => {
  recognition.start(); // 开始语音识别

  recognition.onresult = (event) => {
    const speechResult = event.results[0][0].transcript;
    resultDiv.textContent = `语音识别结果：${speechResult}`;
  };

  recognition.onerror = (event) => {
    resultDiv.textContent = '发生错误：' + event.error;
  };
});