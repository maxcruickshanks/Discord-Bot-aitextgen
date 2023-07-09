import os
import shutil
import subprocess
import sys

import torch
from torch.optim import AdamW
from torch.utils.data import DataLoader
from tqdm.auto import tqdm
from transformers import get_linear_schedule_with_warmup

import pytorch_lightning as pl


class ATGTransformer(pl.LightningModule):
    """
    A training module for aitextgen.
    """

    def __init__(self, model, dataset, hparams, tokenizer):
        super(ATGTransformer, self).__init__()
        self.model, self.dataset, self.tokenizer = (
            model,
            dataset,
            tokenizer,
        )
        self.save_hyperparameters(hparams)

    def forward(self, inputs):
        return self.model(**inputs, return_dict=False)

    def training_step(self, batch, batch_num):
        outputs = self({"input_ids": batch, "labels": batch})
        loss = outputs[0]

        return {"loss": loss}

    def train_dataloader(self):
        return DataLoader(
            self.dataset,
            batch_size=self.hparams["batch_size"],
            shuffle=True,
            pin_memory=self.hparams["pin_memory"],
            num_workers=self.hparams["num_workers"],
        )

    def configure_optimizers(self):
        "Prepare optimizer"

        no_decay = ["bias", "LayerNorm.weight"]
        optimizer_grouped_parameters = [
            {
                "params": [
                    p
                    for n, p in self.model.named_parameters()
                    if not any(nd in n for nd in no_decay)
                ],
                "weight_decay": self.hparams["weight_decay"],
            },
            {
                "params": [
                    p
                    for n, p in self.model.named_parameters()
                    if any(nd in n for nd in no_decay)
                ],
                "weight_decay": 0.0,
            },
        ]
        optimizer = AdamW(
            optimizer_grouped_parameters,
            lr=self.hparams["learning_rate"],
            eps=self.hparams["adam_epsilon"],
        )

        return optimizer