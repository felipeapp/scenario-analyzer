<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Processamento do Resultado da Avalia��o Institucional</h2>
<h:form>
	<div class="descricaoOperacao">
			Caro usu�rio,<br />
			<p>Abaixo est�o listados as datas de aplica��o dos Formul�rios de
				Avalia��o Institucional. Selecione qual deseja processar o c�lculo
				das m�dias e desvios padr�es das notas dadas.
			</p>
		</div>
			
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Processar Avalia��o Institucional
	</div>
	<table class="formulario" width="95%">
		<caption>Lista de Avalia��es Institucionais</caption>
		<thead>
			<tr>
				<td style="text-align: center">Ano-Per�odo</td>
				<td>Formul�rio</td>
				<td>Perfil do Entrevistado</td>
				<td style="text-align: center">Ensino � Dist�ncia</td>
				<td style="text-align: center">Per�odo de Resposta</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{calendarioAvaliacaoInstitucionalBean.allPassivelProcessamento}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center">${item.ano}.${item.periodo}</td>
					<td>${item.formulario.titulo}</td>
					<td>${item.formulario.descricaoTipoAvaliacao }</td>
					<td style="text-align: center"><ufrn:format valor="${item.formulario.ead}" type="simNao" /></td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.inicio}" /> � <ufrn:format type="data" valor="${item.fim}"/></td>
					<td width="5%">
						<h:commandLink action="#{processamentoAvaliacaoInstitucional.selecionarFormulario}" id="processarFormulario">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Processar Avalia��o Institucional"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="7">
					<h:commandButton action="#{processamentoAvaliacaoInstitucional.cancelar}" value="Cancelar" onclick="#{ confirm }" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>