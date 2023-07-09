from aitextgen import aitextgen

file_name = "current_context.txt"
context = open(file_name, "r").read()

ai = aitextgen(
    model_folder="trained_model",
    tokenizer_file="aitextgen.tokenizer.json",
    to_gpu=False)
ai.generate(1, prompt=context, min_length=128, max_length=128)