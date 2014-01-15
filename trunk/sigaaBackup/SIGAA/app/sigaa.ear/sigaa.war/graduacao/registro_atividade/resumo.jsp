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

	<h:form id="form">
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
			<td> ${registroAtividade.obj.ano}.${registroAtividade.obj.periodo} </td>
		</tr>

		<c:if test="${ registroAtividade.informarDocentesEnvolvidos }">
			<c:if test="${(!registroAtividade.obj.componente.atividadeComplementar || (registroAtividade.obj.componente.atividadeComplementar && registroAtividade.obj.componente.temOrientador)) && not empty registroAtividade.obj.registroAtividade}">
			<tr>
				<th> Orientador(es): </th>
				<td>
					<c:forEach var="orientacao" items="#{registroAtividade.obj.registroAtividade.orientacoesAtividade}">
						${orientacao.nome} - ${orientacao.cargaHoraria}h - ${orientacao.descricaoTipo} <br/>
					 </c:forEach>
				</td>
			</tr>
			</c:if>

			<c:if test="${ registroAtividade.estagio }">
				<tr>
					<th> Coordenador de Estágio: </th>
					<td> ${registroAtividade.obj.registroAtividade.coordenador.pessoa.nome}  </td>
				</tr>
				<tr>
					<th> Supervisor de Campo: </th>
					<td> ${registroAtividade.obj.registroAtividade.supervisor} </td>
				</tr>
			</c:if>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao or registroAtividade.validacao}">
			<c:if test="${registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal and not registroAtividade.dispensa}">
			<tr>
				<th> Nota Final: </th>
				<td> ${registroAtividade.obj.mediaFinal} </td>
			</tr>
			</c:if>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao or registroAtividade.validacao or registroAtividade.exclusao}">
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
					
						<h:commandButton value="Confirmar" action="#{registroAtividade.confirmar}" id="botaoConfirmarRegistro"
							rendered="#{ (!(registroAtividade.estagio ||  registroAtividade.trabalhoFimCurso)
										|| (registroAtividade.matricula) || (registroAtividade.exclusao) || (registroAtividade.alteracaoGraduacao && !registroAtividade.obj.consolidada)) }">
						</h:commandButton>

						<h:commandButton value="Confirmar" id="botaoVerDadosProducaoIntelectual"
							action="#{registroAtividade.verDadosProducaoIntelectual}"
							rendered="#{ ( (registroAtividade.estagio ||  registroAtividade.trabalhoFimCurso) &&
							( registroAtividade.consolidacao || registroAtividade.validacao || 
							(registroAtividade.alteracaoGraduacao && registroAtividade.obj.consolidada) ) ) }">
						</h:commandButton>
						
						<h:commandButton value="<< Alterar Dados Informados" action="#{registroAtividade.telaDadosRegistro}" rendered="#{ !registroAtividade.exclusao }" id="botaoAlterarDadosInformados"/>
						
					</c:if>
					
					<c:if test="${registroAtividade.visualizarResumo}">
						<input type="button" value="<< Voltar"  onclick="javascript: history.go(-1)" id="inputVoltar"/>
					</c:if>
					
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{registroAtividade.cancelar}" id="botaoCancelarOperacao"/>
					
				</td>
			</tr>
		</tfoot>
	</table>

	<c:if test="${registroAtividade.necessarioConfirmacaoSenha}">
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	</c:if>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>