AIchat：
aichat使用前后端分离技术，前端首先进行语音识别，将识别出的语音传入后端，在后端进行调用gpt的api，实现实时对话，再传回前端，前端获取回复后传入gptsovits后端实现tts
语音识别使用浏览器自带的识别器，精度较低，后面可能会进行一定的优化
gpt的api使用的是gpt3.5，因为4没额度了
这里gptsovits详见GitHub，https://github.com/RVC-Boss/GPT-SoVITS/blob/main/docs/cn/README.md，稍微调整了一api.py，并允许localhost:5173使用post方法
问题：当前的模型生成语音较慢