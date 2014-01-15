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
		<p>Caro Usu�rio(a),</p><br/>
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO } %>">
		<p>
			Atrav�s desta opera��o � poss�vel gerenciar as solicita��es de turmas 
			de componentes curriculares do seu departamento. 
		</p>
		</ufrn:checkRole>
		
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO } %>">
		<p>
			Atrav�s desta opera��o � poss�vel gerenciar as solicita��es de turmas 
			de componentes curriculares que possuem a <b>coordena��o</b> como unidade respons�vel.
		</p>
		
		</ufrn:checkRole>		
		
		<p> Conv�m, antes de iniciar o gerenciamento das orienta��es, ler atentamente as observa��es abaixo: </p>
		
		<ul>
			<li> A opera��o <b><h:graphicImage value="/img/add2.png" style="overflow: visible;"/> Atender Solicita��o</b> ir� criar uma ou mais turmas a partir das reservas da solicita��o selecionada.
				Ao atender uma solicita��o de turma � poss�vel ainda adicionar reservas de vagas para outros cursos 
				na mesma turma desde que exista solicita��o aberta do mesmo componente e no mesmo hor�rio.
			</li>
			
			<li>
			A opera��o <b><h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/> Solicitar Altera��o de Hor�rio</b> deve ser executada quando voc� desejar criar a turma 
			em um hor�rio diferente do que foi solicitado. O departamento n�o pode alterar o hor�rio da solicita��o.
			Ao solicitar altera��o de hor�rio a situa��o da solicita��o ficar� "Solicitado Altera��o"; 	
			</li>
			   
			<li>
			A opera��o <b><h:graphicImage  value="/img/addUnd.gif" style="overflow: visible;" /> Adicionar Reserva em Turma Existente</b> deve ser executada quando voc� desejar adicionar
			as reservas da solicita��o selecionada em uma turma que j� foi criada a partir de outra reserva.
			Para que seja poss�vel � necess�rio que a turma seja do mesmo componente curricular da solicita��o e tenha o mesmo hor�rio. 
			</li>
			
			<li>
			A opera��o <b><h:graphicImage value="/img/view.gif" style="overflow: visible;"/> Visualizar Solicita��o de Turma</b> exibe detalhes da solicita��o selecionada e, caso esta j� tenha sido atendida, 
			da(s) turma(s) criada(s) a partir desta solicita��o. 
			</li>
			
			<li>
			A opera��o <b><h:graphicImage value="/img/delete.png" style="overflow: visible;"/> Negar Solicita��o</b> deve ser executada quando o chefe, por algum motivo, n�o poder� criar a turma solicitada.
			Nesta opera��o ele deve ainda entrar com a justificativa de n�o poder criar a turma. 
			</li>
			
		</ul>
</div>


	<h:outputText value="#{solicitacaoTurma.create}"/>
	<h:form id="formBusca">
	<table class="formulario" width="70%">
		<caption>Consultar Solicita��es</caption>

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
			<td><label for="checkHorario" onclick="$('formBusca:checkHorario').checked = !$('formBusca:checkHorario').checked;">Hor�rio:</label></td>
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
			<h:graphicImage value="/img/add2.png" style="overflow: visible;"/>: Atender Solicita��o
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: Solicitar Altera��o de Hor�rio
			<h:graphicImage  value="/img/addUnd.gif" style="overflow: visible;" />: Adicionar Reserva em Turma Existente
			<br/>
			<h:graphicImage value="/img/delete.png" style="overflow: visible;"/>: Negar Solicita��o
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicita��o de Turma
		</div>
	</center>
	<br/>

	<h:form id="formLista">
	<table class="listagem"  width="99%">
		<caption>Solicita��es Encontradas (${ fn:length(analiseSolicitacaoTurma.solicitacoes) })</caption>
		<thead>
			<tr>
				<td>Componente Curricular</td>
				<td>Solicitante</td>
				<td style="text-align: center;">Data da Solicita��o</td>
				<td>Tipo</td>
				<td>Hor�rio</td>
				<td style="text-align: right;">Vagas</td>
				<td>Situa��o</td>
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
						<h:graphicImage url="/img/add2.png" title="Atender Solicita��o" />
					</h:commandLink>
					</c:if>
				</td>
				<td valign="middle" >
					<c:choose>
						<c:when test="${solicitacao.passivelAtendimento and (turmaGraduacaoBean.periodoCadastroTurma or turmaGraduacaoBean.calendarioVigente.periodoAjustesTurmas or turmaGraduacaoBean.portalCoordenadorGraduacao)}">
							<h:commandLink  action="#{analiseSolicitacaoTurma.solicitarAlteracaoHorario}" id="btnSolicitar">
								<f:param name="id" value="#{solicitacao.id }" />
								<h:graphicImage url="/img/alterar_old.gif" title="Solicitar Altera��o de Hor�rio" />
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
						<h:graphicImage url="/img/view.gif" title="Visualizar Solicita��o de Turma" />
					</h:commandLink>
				</td>
				<td>
					<c:if test="${solicitacao.passivelNegacaoSolicitacao and turmaGraduacaoBean.periodoCadastroTurma}">
					<h:commandLink styleClass="noborder" title="Negar Solicita��o" 
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
					<h:commandButton value="Gerar Relat�rio" action="#{analiseSolicitacaoTurma.imprimir}"/>
					<h:commandButton value="Cancelar" action="#{analiseSolicitacaoTurma.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>