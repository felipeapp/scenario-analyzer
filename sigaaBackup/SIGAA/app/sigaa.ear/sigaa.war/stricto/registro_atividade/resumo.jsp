<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario tbody tr th { font-weight: bold; }
	span.positivo { color: #292; font-weight: bold;}
	span.negativo { color: #922; font-weight: bold;}

	table.formulario tr.matriculaCompulsoria td {
		background: #EEE;
		text-align: center;
		font-style: italic;
	}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> > ${registroAtividade.descricaoOperacao} &gt; Confirmação </h2>

	<c:set var="discente" value="#{registroAtividade.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form prependId="false">
	<table class="formulario" style="width: 90%">
		<caption> Dados do Registro </caption>
		<tbody>
		<tr>
			<th> Atividade: </th>
			<td> ${registroAtividade.obj.componente.codigoNome} </td>
		</tr>
		<tr>
			<th> Tipo da Atividade: </th>
			<td> ${registroAtividade.obj.componente.tipoAtividade.descricao} </td>
		</tr>

		<c:if test="${ registroAtividade.obj.registroAtividade.matriculaCompulsoria }">
			<tr class="matriculaCompulsoria">
				<td colspan="2">
					A ser tratada como matrícula compulsória!
				</td>
			</tr>
		</c:if>

		<tr>
			<th> Ano-Período: </th>
			<td> 
				${registroAtividade.obj.ano}.${registroAtividade.obj.periodo}
				<c:if test="${registroAtividade.renovacao}">
					<small> (renovando para <b>${registroAtividade.calendario.anoPeriodo}</b>) </small>
				</c:if>
			 </td>
		</tr>
		
		<c:if test="${not empty registroAtividade.obj.anoInicio}">
		<tr>
			<th> Data de Início: </th> 
			<td>
			<ufrn:format type="mes" valor="${registroAtividade.obj.mes > 0 ? registroAtividade.obj.mes - 1 : registroAtividade.obj.mes}" /> / ${registroAtividade.obj.anoInicio}
			</td>
		</tr>
		</c:if>
		
		<c:if test="${(registroAtividade.alteracao && (registroAtividade.obj.aprovado || registroAtividade.obj.aproveitado)) || (registroAtividade.consolidacao || registroAtividade.validacao)}">		
			<tr>
				<th> Data Final: </th>
				<td>
				   <ufrn:format type="mes" valor="${registroAtividade.obj.mesFim > 0 ? registroAtividade.obj.mesFim - 1 : registroAtividade.obj.mesFim}" /> / ${registroAtividade.obj.anoFim}
				</td>
			</tr>
		</c:if>

		<c:if test="${ not registroAtividade.alteracao && registroAtividade.informarDocentesEnvolvidos }">
			<c:if test="${!registroAtividade.obj.componente.atividadeComplementar}">
			<tr>
				<th> Orientador: </th>
				<td>
					<c:forEach var="orientacao" items="#{registroAtividade.obj.registroAtividade.orientacoesAtividade}">
						${orientacao.orientador.siapeNome}<br/>
					</c:forEach>
				</td>
			</tr>
			</c:if>
		</c:if>

		<c:if test="${ (registroAtividade.consolidacao or registroAtividade.validacao) and not registroAtividade.alteracao 
			and registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal}">
			<c:if test="${registroAtividade.obj.metodoConceito and not registroAtividade.dispensa}">
			<tr>
				<th> Conceito: </th>
				<td> ${registroAtividade.obj.conceitoChar} </td>
			</tr>
			</c:if>
			<c:if test="${registroAtividade.obj.metodoNota}">
			<tr>
				<th> Nota: </th>
				<td> ${registroAtividade.obj.mediaFinal} </td>
			</tr>
			</c:if>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao or registroAtividade.validacao or registroAtividade.exclusao }">
			<tr>
				<th> Resultado: </th>
				<td style="padding: 8px;">
					<span class="${ registroAtividade.obj.aprovado || registroAtividade.obj.matriculado || registroAtividade.obj.dispensa ? 'positivo' : 'negativo' }">
					${registroAtividade.obj.situacaoMatricula.descricao}
					</span>
				</td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${!registroAtividade.visualizarResumo}">
						<h:commandButton value="Confirmar" action="#{registroAtividade.confirmar}" id="confirmar"
							rendered="#{ (!(registroAtividade.estagio ||  registroAtividade.trabalhoFimCurso)
										|| (registroAtividade.matricula) || (registroAtividade.alteracao) || (registroAtividade.exclusao))}">
						</h:commandButton>

						<h:commandButton value="Confirmar" id="confirmarProducao"
							action="#{registroAtividade.verDadosProducaoIntelectual}"
							rendered="#{((registroAtividade.estagio ||  registroAtividade.trabalhoFimCurso) &&
							(registroAtividade.consolidacao || registroAtividade.validacao) )}">
						</h:commandButton>

						<h:commandButton value="<< Alterar Dados Informados" action="#{registroAtividade.telaDadosRegistro}" rendered="#{ !registroAtividade.exclusao && !registroAtividade.renovacao }" id="alterar"/>
						<h:commandButton value="<< Selecionar Outra Atividade" action="#{registroAtividade.telaAtividades}" rendered="#{ registroAtividade.renovacao }" id="selecionarOutro"/>
					</c:if>
					<c:if test="${registroAtividade.visualizarResumo}">
						<input type="button" value="<< Voltar"  onclick="javascript: history.go(-1)" id="voltar"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{registroAtividade.cancelar}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>

	<c:if test="${registroAtividade.necessarioConfirmacaoSenha}">
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	</c:if>

	</h:form>
	
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>