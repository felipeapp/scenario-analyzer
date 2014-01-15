<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<jsp:useBean id="sigaaSubSistemas" class="br.ufrn.arq.seguranca.SigaaSubsistemas" scope="page"/>

<f:view>

	<ufrn:checkSubSistema subsistema="<%=sigaaSubSistemas.PORTAL_DOCENTE.getId()%>">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</ufrn:checkSubSistema>

	<ufrn:checkSubSistema subsistema="<%=sigaaSubSistemas.PORTAL_COORDENADOR.getId()%>">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</ufrn:checkSubSistema>
	
	<h2><ufrn:subSistema /> > Turmas Solicitadas</h2>


<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p><br/>
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO } %>">
		<p>
			Através desta operação é possível gerenciar as solicitações de turmas 
			de componentes curriculares do seu departamento. 
		</p>
		</ufrn:checkRole>
		
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO } %>">
		<p>
			Através desta operação é possível gerenciar as solicitações de turmas 
			de componentes curriculares que possuem a <b>coordenação</b> como unidade responsável.
		</p>
		
		</ufrn:checkRole>		
		
		<p> Convém, antes de iniciar o gerenciamento das orientações, ler atentamente as observações abaixo: </p>
		
		<ul>
			<li> A operação <b><h:graphicImage value="/img/add2.png" style="overflow: visible;"/> Atender Solicitação</b> irá criar uma ou mais turmas a partir das reservas da solicitação selecionada.
				Ao atender uma solicitação de turma é possível ainda adicionar reservas de vagas para outros cursos 
				na mesma turma desde que exista solicitação aberta do mesmo componente e no mesmo horário.
			</li>
			
			<li>
			A operação <b><h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/> Solicitar Alteração de Horário</b> deve ser executada quando você desejar criar a turma 
			em um horário diferente do que foi solicitado. O departamento não pode alterar o horário da solicitação.
			Ao solicitar alteração de horário a situação da solicitação ficará "Solicitado Alteração"; 	
			</li>
			   
			<li>
			A operação <b><h:graphicImage  value="/img/addUnd.gif" style="overflow: visible;" /> Adicionar Reserva em Turma Existente</b> deve ser executada quando você desejar adicionar
			as reservas da solicitação selecionada em uma turma que já foi criada a partir de outra reserva.
			Para que seja possível é necessário que a turma seja do mesmo componente curricular da solicitação e tenha o mesmo horário. 
			</li>
			
			<li>
			A operação <b><h:graphicImage value="/img/view.gif" style="overflow: visible;"/> Visualizar Solicitação de Turma</b> exibe detalhes da solicitação selecionada e, caso esta já tenha sido atendida, 
			da(s) turma(s) criada(s) a partir desta solicitação. 
			</li>
			
			<li>
			A operação <b><h:graphicImage value="/img/delete.png" style="overflow: visible;"/> Negar Solicitação</b> deve ser executada quando o chefe, por algum motivo, não poderá criar a turma solicitada.
			Nesta operação ele deve ainda entrar com a justificativa de não poder criar a turma. 
			</li>
			
		</ul>
</div>


	<h:outputText value="#{solicitacaoTurma.create}"/>
	<h:form id="formBusca">
	<table class="formulario" width="70%">
		<caption>Consultar Solicitações</caption>

		<tr>
			<td width="2%"><h:selectBooleanCheckbox value="#{analiseSolicitacaoTurma.filtroCurso}" id="checkCurso" styleClass="noborder"/></td>
			<td width="13%"><label for="checkCurso" onclick="$('formBusca:checkCurso').checked = !$('formBusca:checkCurso').checked;">Curso:</label></td>
			<td>
			<h:selectOneMenu id="cursos" value="#{analiseSolicitacaoTurma.idCurso}" onchange="$('formBusca:checkCurso').checked = true;">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{analiseSolicitacaoTurma.allCursosSolicitantesCombo}"/>
			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td width="2%"><h:selectBooleanCheckbox value="#{analiseSolicitacaoTurma.filtroComponente}" id="checkComponente" styleClass="noborder"/></td>
			<td><label for="checkComponente" onclick="$('formBusca:checkComponente').checked = !$('formBusca:checkComponente').checked;">Componente:</label></td>
			<td>
			<h:selectOneMenu id="comp" value="#{analiseSolicitacaoTurma.idComponente}"  onchange="$('formBusca:checkComponente').checked = true;">
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{analiseSolicitacaoTurma.allComponentesSolicitantesCombo}"/>
			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td width="2%"><h:selectBooleanCheckbox value="#{analiseSolicitacaoTurma.filtroHorario}" id="checkHorario" styleClass="noborder"/></td>
			<td><label for="checkHorario" onclick="$('formBusca:checkHorario').checked = !$('formBusca:checkHorario').checked;">Horário:</label></td>
			<td>
				<h:inputText value="#{analiseSolicitacaoTurma.horario}" id="hor" onfocus="$('formBusca:checkHorario').checked = true;" onkeyup="CAPS(this);"/>
			</td>
		</tr>
		<tr>
			<td width="2%"><h:selectBooleanCheckbox value="#{analiseSolicitacaoTurma.filtroDocente}" id="checkDocente" styleClass="noborder"/></td>
			<td><label for="checkDocente" onclick="$('formBusca:checkDocente').checked = !$('formBusca:checkDocente').checked;">Docente:</label></td>
			<td>
				<h:inputText value="#{analiseSolicitacaoTurma.docente.pessoa.nome}" id="nome" size="60" onfocus="javascript:$('formBusca:checkDocente').checked = true;" />
			 	<h:inputHidden value="#{analiseSolicitacaoTurma.docente.id}" id="idServidor" />

				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
			</td>
		</tr>

		<tfoot>
			<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{analiseSolicitacaoTurma.buscar}" />
				<h:commandButton value="Listar Todos" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesTodas}" />
			</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br/>

	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/add2.png" style="overflow: visible;"/>: Atender Solicitação
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: Solicitar Alteração de Horário
			<h:graphicImage  value="/img/addUnd.gif" style="overflow: visible;" />: Adicionar Reserva em Turma Existente
			<br/>
			<h:graphicImage value="/img/delete.png" style="overflow: visible;"/>: Negar Solicitação
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação de Turma
		</div>
	</center>
	<br/>

	<h:form id="formLista">
	<table class="listagem"  width="99%">
		<caption>Solicitações Encontradas (${ fn:length(analiseSolicitacaoTurma.solicitacoes) })</caption>
		<thead>
			<tr>
				<td>Componente Curricular</td>
				<td>Solicitante</td>
				<td style="text-align: center;">Data da Solicitação</td>
				<td>Tipo</td>
				<td>Horário</td>
				<td style="text-align: right;">Vagas</td>
				<td>Situação</td>
				<td width="2%" colspan="5"></td>
			</tr>
		</thead>

		<tbody>
		<c:forEach var="solicitacao" items="#{analiseSolicitacaoTurma.solicitacoes}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td>${solicitacao.componenteCurricular.codigo} - ${solicitacao.componenteCurricular.nome}</td>
				<td>${solicitacao.curso.descricao}</td>
				<td style="text-align: center;"><ufrn:format type="data" valor="${solicitacao.dataCadastro}" /></td>
				<td>${solicitacao.tipoString}</td>
				<td>${solicitacao.horario}</td>
				<td style="text-align: right;">${solicitacao.vagas}</td>
				<td>${solicitacao.situacaoString}</td>

				<td valign="middle" >
					<c:if test="${solicitacao.passivelAtendimento and (turmaGraduacaoBean.periodoCadastroTurma or turmaGraduacaoBean.calendarioVigente.periodoAjustesTurmas)}">
					<h:commandLink action="#{turmaGraduacaoBean.iniciarAtendimentoSolicitacao}" id="btnAtender">
						<f:param name="id" value="#{solicitacao.id }" />
						<h:graphicImage url="/img/add2.png" title="Atender Solicitação" />
					</h:commandLink>
					</c:if>
				</td>
				<td valign="middle" >
					<c:choose>
						<c:when test="${solicitacao.passivelAtendimento and (turmaGraduacaoBean.periodoCadastroTurma or turmaGraduacaoBean.calendarioVigente.periodoAjustesTurmas or turmaGraduacaoBean.portalCoordenadorGraduacao)}">
							<h:commandLink  action="#{analiseSolicitacaoTurma.solicitarAlteracaoHorario}" id="btnSolicitar">
								<f:param name="id" value="#{solicitacao.id }" />
								<h:graphicImage url="/img/alterar_old.gif" title="Solicitar Alteração de Horário" />
							</h:commandLink>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>		
				</td>
				<td>
					<c:if test="${solicitacao.passivelAtendimento and turmaGraduacaoBean.periodoCadastroTurma}">
					<h:commandLink styleClass="noborder" title="Adicionar Reserva em Turma Existente" action="#{turmaGraduacaoBean.atenderReservaTurmaExistente}"  id="btnAdicionar">
						<h:graphicImage url="/img/addUnd.gif"/>
						<f:param name="id" value="#{solicitacao.id}"/>
						<f:param name="adicionarReservas" value="true"/>
					</h:commandLink>
					</c:if>
				</td>
				<td valign="middle" >
					<h:commandLink action="#{solicitacaoTurma.view}" id="btnVisualizar">
						<f:param name="id" value="#{solicitacao.id }" />
						<f:param name="isListaSolicitacao" value="false"/>
						<h:graphicImage url="/img/view.gif" title="Visualizar Solicitação de Turma" />
					</h:commandLink>
				</td>
				<td>
					<c:if test="${solicitacao.passivelNegacaoSolicitacao and turmaGraduacaoBean.periodoCadastroTurma}">
					<h:commandLink styleClass="noborder" title="Negar Solicitação" 
					action="#{analiseSolicitacaoTurma.iniciarNegarSolicitacao}"  id="btnNegarSolicitacao">
						<h:graphicImage url="/img/delete.png"/>
						<f:param name="id" value="#{solicitacao.id}"/>
					</h:commandLink>
					</c:if>
				</td>

			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="12" style="text-align: center">
					<h:commandButton value="Gerar Relatório" action="#{analiseSolicitacaoTurma.imprimir}"/>
					<h:commandButton value="Cancelar" action="#{analiseSolicitacaoTurma.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>