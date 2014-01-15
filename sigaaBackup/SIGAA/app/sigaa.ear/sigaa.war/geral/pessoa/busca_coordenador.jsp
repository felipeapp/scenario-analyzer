<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema /> &gt; Dados Pessoais dos Discentes </h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Coordenador,</b></p>
		<p>
			Selecione um discente abaixo para poder atualizar os dados cadastrais.
		</p>
	</div>
	
	<div class="infoAltRem" style="width: 80%">
	<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecionar Discente
	</div>
	
	<h:form>
	<c:if test="${not empty alteracaoDadosDiscente.discentesCurso }">
	<table class="listagem" id="lista-turmas-alunoscadastrados" style="width: 80%">
		<caption>Discentes Ingressantes (${fn:length(alteracaoDadosDiscente.discentesCurso)} discente(s) ingressante(s)) </caption>
		<tbody>
			<c:forEach items="#{alteracaoDadosDiscente.discentesCurso}" var="aluno" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="">
						${aluno.matriculaNome}
					</td>
					<td width="3%">
						<h:commandLink action="#{alteracaoDadosDiscente.selecionaDiscenteCoordenador}" title="Selecionar Discente" id="selecionaDiscente">
							<f:param value="#{aluno.id}" name="id"/>
							<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="2" align="center">
					 <h:commandButton value="Cancelar" action="#{ alteracaoDadosDiscente.cancelar }" onclick="#{ confirm }" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	</h:form>
	<br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
