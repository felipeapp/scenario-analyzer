<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> > Inativar Metodologia</h2>


<div class="descricaoOperacao">
	Informe até qual Ano e Período esta metodologia será usada. Por exemplo, considere uma metodolgia que foi criada em 2008.1. Se selecionar 2010.1 como sendo o ano/periodo final, todas as turmas de 2008.1 até 2010.1 (incluindo as turmas de 2010.1) usarão essa metodologia, de 2010.2 em diante não será usada.
</div>

<h:form>

	<c:set var="nomeOperacaoVisualizacao" value="Resumo da metodologia"/>

	<%@include file="/ead/MetodologiaAvaliacao/detalhesMetodologia.jsp"%>
	
	<br>

	<table class="formulario" width="100%">
		<caption>Inativar Metodologia</caption>
		
		<tbody>
		
			<tr>
				<th class="obrigatorio">Ano-Período final:</th>
				<td><h:inputText value="#{metodologiaAvaliacaoEad.obj.anoFim}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" />.<h:inputText value="#{metodologiaAvaliacaoEad.obj.periodoFim}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="2">
						<center>
							<h:commandButton value="Inativar" action="#{metodologiaAvaliacaoEad.inativar}"/>
							
							<h:commandButton id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}">
								<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="4" /> 
							</h:commandButton>
														
							
							<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
						</center>						
				</td>
			</tr>
		</tfoot>		
		
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>