<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2 class="title"> <ufrn:subSistema/> &gt; Solicita��o de Abertura de Turma &gt; Visualiza��o</h2>
	<h:outputText value="#{solicitacaoTurma.create}"/>

	<table class="visualizacao">
		<caption>Visualiza��o da Solicita��o</caption>

		<tr><td colspan="2" class="subFormulario">Dados do Solicitante</td></tr>
		<tr>
			<th width="25%"> Curso solicitante: </th>
			<td> ${solicitacaoTurma.obj.curso.descricao } </td>
		</tr>
		<tr>
			<th> Usu�rio que solicitou: </th>
			<td> ${solicitacaoTurma.obj.registroEntrada.usuario.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Ramal de contato: </th>
			<td> ${solicitacaoTurma.obj.registroEntrada.usuario.ramal } </td>
		</tr>
		<tr>
			<th> Email de contato: </th>
			<td> ${solicitacaoTurma.obj.registroEntrada.usuario.email } </td>
		</tr>

		<tr><td colspan="2" class="subFormulario">Dados da Solicita��o de Turma</td></tr>

		<tr>
			<th width="20%"> Componente Curricular: </th>
			<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
		</tr>
		<tr>
			<th> C�digo: </th>
			<td> ${solicitacaoTurma.obj.componenteCurricular.codigo } </td>
		</tr>
		<tr>
			<th> Tipo: </th>
			<td> ${solicitacaoTurma.obj.componenteCurricular.tipoComponente.descricao } </td>
		</tr>
		<c:if test="${solicitacaoTurma.obj.componenteCurricular.bloco}">
		<tr>
			<th> Subunidades: </th>
			<td>
				<c:forEach items="${solicitacaoTurma.obj.componenteCurricular.subUnidades}" var="subunidade">
					${subunidade} - ${subunidade.chTotal}h  <br/>						
				</c:forEach> 
			</td>
		</tr>
		</c:if>
		<tr>
			<th> Carga Hor�ria: </th>
			<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.chTotal } horas </td>
		</tr>
		<c:if test="${ !solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<tr>
				<th>Hor�rio: </th>
				<td> ${solicitacaoTurma.obj.horario}</td>
			</tr>
		</c:if>
		<tr>
			<th>Ano-Per�odo: </th>
			<td> ${solicitacaoTurma.obj.ano}-${solicitacaoTurma.obj.periodo}</td>
		</tr>
		<tr>
			<th>Situa��o: </th>
			<td> ${solicitacaoTurma.obj.situacaoString}</td>
		</tr>
		
		<c:if test="${solicitacaoTurma.obj.negadaOuSolicitadoAlteracaoHorario }">
		<tr>
			<th>Observa��es: </th>
			<td> <b>${solicitacaoTurma.obj.observacoes}</b></td>
		</tr>
		</c:if>
		
		<c:if test="${ solicitacaoTurma.obj.observacaoSugestao != null}">
		<tr>
			<th>Observa��o da Sugest�o de Turma: </th>
			<td> ${solicitacaoTurma.obj.observacaoSugestao}</td>
		</tr>
		</c:if>
				
		<c:if test="${ !(solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias) }">
		<tr><td colspan="2">
			<table width="100%">
				<caption>Reservas Solicitadas</caption>
		
				<thead>
					<tr>
					<td>Curso</td>
					<td  align="center">Turno</td>
					<td>Modalidade</td>
					<td>Habilita��o/�nfase</td>
					<td width="5%" align="center">Vagas Solicitadas</td>
					<td width="5%" align="center">Vagas Atendidas</td>
					<td width="7%" align="center">Ingressantes <br> Solic./Atend.</td>
					<td width="8%" >C�digo da Turma</td>
					</tr>
				</thead>
				<c:if test="${not empty solicitacaoTurma.obj.reservas}" >
				<c:forEach var="reserva" items="${solicitacaoTurma.obj.reservas}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${reserva.matrizCurricular.curso.descricao}</td>
						<td align="center">${reserva.matrizCurricular.turno.sigla}</td>
						<td>${reserva.matrizCurricular.grauAcademico.descricao}</td>
						<td>
							${reserva.matrizCurricular.habilitacao.nome}
						    ${reserva.matrizCurricular.enfase.nome}
					    </td>
						<td align="center">${reserva.vagasSolicitadas}</td>
						<td align="center">${reserva.vagasReservadas}</td>
						<td align="center">
							${reserva.vagasSolicitadasIngressantes != null ? reserva.vagasSolicitadasIngressantes : 0}
							 / 
							${reserva.vagasReservadasIngressantes != null ? reserva.vagasReservadasIngressantes : 0}
						</td>
						<td>${reserva.turma.codigo}</td>
					</tr>
				</c:forEach>
				</c:if>
				<c:if test="${solicitacaoTurma.obj.atendidaOuAtendidaParcialmente && fn:length(solicitacaoTurma.obj.reservas) == 0}" >
					<tr>
						<td colspan="7" align="center"> N�o h� nenhuma reserva para esta turma. </td>
					</tr>
				</c:if>
				<c:if test="${!solicitacaoTurma.obj.atendidaOuAtendidaParcialmente}">
					<tr>
						<td colspan="7" align="center" style="color: red;"> Esta solicita��o de turma ainda n�o foi atendida. N�o h� nenhuma reserva atendida. </td>
					</tr>
				</c:if>

			</table>
		</td></tr>
		</c:if>
		
		
		<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<tr><td colspan="2" class="subFormulario">Discentes da Solicita��o</td></tr>
			
			<c:forEach var="discenteVar" items="${solicitacaoTurma.obj.discentes}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td colspan="2">
					${discenteVar.discenteGraduacao}				
				</td>
			</tr>
			</c:forEach>
		</c:if>

		<c:if test="${solicitacaoTurma.obj.periodo >= 3}">
			<tr><td colspan="2" class="subFormulario">Discentes Interessados no Cursos Especiais de F�rias</td></tr>
			<c:forEach items="${solicitacaoTurma.obj.discentes}" var="discenteVar" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td></td>
				<td>${discenteVar.discenteGraduacao}</td>
			</tr>
			</c:forEach>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:form>
						<h:commandButton value="<< Voltar" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoes}" id="voltarChefeDepto" rendered="#{acesso.chefeDepartamento && not solicitacaoTurma.alterarSolicitacao }"/>
						<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.listaSolicitacoes}" id="voltar" rendered="#{(not usuario.discenteAtivo.graduacao && not acesso.chefeDepartamento) ||  solicitacaoTurma.alterarSolicitacao }"/>
						<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.iniciarListaSolicitacoesCursoDiscente}" id="voltarDiscente" rendered="#{usuario.discenteAtivo.graduacao && not acesso.chefeDepartamento}"/>
					</h:form>
				</td>
			</tr>
		</tfoot>

	</table>


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>