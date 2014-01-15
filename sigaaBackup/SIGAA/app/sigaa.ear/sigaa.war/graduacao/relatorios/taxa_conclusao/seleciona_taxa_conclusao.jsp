<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relat�rio de Taxa de Conclus�o</h2>
	
	<div class="descricaoOperacao">
		<p><b>Taxa de conclus�o dos cursos de gradua��o</b></p>
		<br>
		<p>A taxa de conclus�o dos cursos de gradua��o � um indicador calculado anualmente por meio da raz�o
		entre diplomados e ingressos. O valor de TCG n�o expressa diretamente as taxas de sucesso observadas nos 
		cursos da universidade, ainda que haja uma rela��o estreita com fen�meno de reten��o e evas�o. Na verdade
		TCG tamb�m contempla a efici�ncia com que a universidade preenche as suas vagas ociosas decorrentes do abandono
		dos cursos.</p>
		
		<br>
		<p>
			<b>Taxa de conclus�o dos cursos de gradua��o (TCG):</b> rela��o entre o total de diplomados nos cursos
			de gradua��o presenciais (DIP) num determinado ano e o total de vagas de ingresso oferecidas pela institui��o (ING<sub>5</sub>)
			cinco anos antes.
		</p>
		<br>
		<p style="text-align: center; font-weight: bold;">TCG = DIP/ING<sub>5</sub></p>
		
	</div>
	
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioTaxaConclusao.create}" />
		<table class="formulario" width="40%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">
						 Ano In�cio:
					</th>
					<td align="left">
					    <h:inputText value="#{relatorioTaxaConclusao.ano}" id="anoInicio" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
					    &nbsp;
					    Ano Fim:<span class="obrigatorio"> </span> 
					    <h:inputText value="#{relatorioTaxaConclusao.anoFim}" id="anoFim" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relat�rio" id="gerarRelatorio" action="#{relatorioTaxaConclusao.gerarRelatorioTaxaConclusao}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioTaxaConclusao.cancelar}" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>		
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>