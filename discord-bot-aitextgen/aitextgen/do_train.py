from aitextgen import aitextgen
from aitextgen.utils import build_gpt2_config
from aitextgen.tokenizers import train_tokenizer
import torch

file_name = "trained_contexts.txt"
train_tokenizer(file_name)

config = build_gpt2_config(vocab_size=3000, max_length=128, dropout=0.0, n_embd=768, n_layer=16, n_head=16)
torch.set_float32_matmul_precision('high')
ai = aitextgen(config=config,
      #model_folder="trained_model",
      tokenizer_file="aitextgen.tokenizer.json",
      to_gpu=True)
ai.train(file_name,
         line_by_line=False,
         from_cache=False,
         num_steps=10000000000000000000,
         num_workers=16,
         generate_every=5000,
         save_every=5000,
         save_gdrive=False,
         learning_rate=1e-3,
         batch_size=16,
         n_gpu=1
         )