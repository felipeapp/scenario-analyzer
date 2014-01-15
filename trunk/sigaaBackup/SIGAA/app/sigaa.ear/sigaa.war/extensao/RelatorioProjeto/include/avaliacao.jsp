<table class="formulario" style="width: 100%">
	
		<tr>
			<td colspan="2"><b> Público Estimado:</b>
				<h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.publicoEstimado}" /> pessoas
				<ufrn:help img="/img/ajuda.gif">Público estimado informado durante o cadastro da proposta do projeto.</ufrn:help>
			</td>	
		</tr>
		
		<tr>
			<td colspan="2"><b> Público real atingido: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputText title="Público real atingido" value="#{relatorioAcaoExtensao.mbean.obj.publicoRealAtingido}" onfocus="setAba('avaliacao')"
					readonly="#{relatorioAcaoExtensao.mbean.readOnly}" size="10" maxlength="9" onkeyup="return formatarInteiro(this)"/> pessoas
			</td>	
		</tr>

		<tr>
			<td>
				<a4j:outputPanel ajaxRendered="true" id="realizacaoAcao">
					<a4j:region rendered="#{ not relatorioAcaoExtensao.mbean.obj.acaoRealizada }">
						<table style="width: 100%">
							<tr>
								<td class="subFormulario">Motivo não realização</td>
							</tr>
							<tr>
								<td colspan="4" width="100%">
									<b> Motivo da não realização desta ação: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
									<h:inputTextarea rows="4" style="width:100%" value="#{relatorioAcaoExtensao.mbean.obj.motivoAcaoNaoRealizada}" 
										readonly="#{relatorioAcaoExtensao.mbean.readOnly}" id="motivoAcaoNaoRealizada"/>
								</td>	
							</tr>
						</table>
					</a4j:region>
				</a4j:outputPanel>
			</td>
		</tr>
		
		<tr>
			<td class="subFormulario">Produtos Gerado</td>
		</tr>

		<tr>
			<td colspan="2"><b> Apresentação em Eventos Científicos: </b>
				<h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputText value="#{relatorioAcaoExtensao.mbean.obj.apresentacaoEventoCientifico}" onfocus="setAba('avaliacao')"
					size="10" maxlength="9" onkeyup="return formatarInteiro(this)"/> apresentações.
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Resumo sobre a apresentação: </b>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoExtensao.mbean.obj.observacaoApresentacao}" 
					onfocus="setAba('avaliacao')"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Artigos Científicos produzidos a partir da ação de extensão: </b>
				<h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputText value="#{relatorioAcaoExtensao.mbean.obj.artigosEventoCientifico}" onfocus="setAba('avaliacao')" 
					size="10" maxlength="9" onkeyup="return formatarInteiro(this)"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Resumo sobre o Artigo: </b>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoExtensao.mbean.obj.observacaoArtigo}" 
					onfocus="setAba('avaliacao')"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Outras produções geradas a partir da ação de Extensão: </b>
				<h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputText value="#{relatorioAcaoExtensao.mbean.obj.producoesCientifico}" onfocus="setAba('avaliacao')" 
					size="10" maxlength="9" onkeyup="return formatarInteiro(this)"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Resumo sobre a Produção: </b>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoExtensao.mbean.obj.observacaoProducao}" 
					onfocus="setAba('avaliacao')"/>
			</td>	
		</tr>

		<tr>
			<td class="subFormulario">Informações do Projeto</td>
		</tr>

		<tr>
			<td colspan="2" ><b> Dificuldades Encontradas: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoExtensao.mbean.obj.dificuldadesEncontradas}" 
					onfocus="setAba('avaliacao')" readonly="#{relatorioAcaoExtensao.mbean.readOnly}" />
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Observações Gerais: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoExtensao.mbean.obj.observacoesGerais}" 
					onfocus="setAba('avaliacao')" readonly="#{relatorioAcaoExtensao.mbean.readOnly}" />
			</td>	
		</tr>

</table>