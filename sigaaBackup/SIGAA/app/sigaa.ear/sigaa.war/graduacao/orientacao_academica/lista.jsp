<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<c:set var="confirmDelete" value="if (!confirm('Deseja realmente remover esta orientação acadêmica?')) return false" scope="request"/>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema/> &gt; Gerenciar Orientações Acadêmicas </h2>
	<h:outputText value="#{orientacaoAcademica.create}" />

	<c:if test="${orientacaoAcademica.gerenciarOrientacoes}">

	<table class="formulario" width="50%">
	<h:form id="formBusca">
	<caption>Busca por Orientação Acadêmica</caption>
	<tbody>
		<tr>
			<td><input type="radio" id="checkOrientador" name="paramBusca" value="orientador" class="noborder"></td>
			<td><label for="checkOrientador">Orientador:</label></td>
			<td>
				<h:inputText value="#{orientacaoAcademica.orientador.pessoa.nome}" id="nome" size="60" onfocus="javascript:$('checkOrientador').checked = true;"/>
				 <h:inputHidden value="#{orientacaoAcademica.orientador.id}" id="idServidor" />

				 <ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>

			</td>
		</tr>
		<tr>
			<td><input type="radio" id="checkDiscente" name="paramBusca" value="discente" class="noborder"></td>
			<td><label for="checkDiscente">Discente:</label></td>
			<td>
				 <h:inputHidden id="idDiscente" value="#{ orientacaoAcademica.discenteBusca.id }"/>
				 <h:inputText id="nomeDiscente" value="#{ orientacaoAcademica.discenteBusca.pessoa.nome }" size="90" onfocus="javascript:$('checkDiscente').checked = true;"/>

				<ajax:autocomplete source="formBusca:nomeDiscente" target="formBusca:idDiscente"
					baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
					indicator="indicatorDiscente" minimumCharacters="3"
					parameters="nivel=${sessionScope.nivel}"
					parser="new ResponseXmlToHtmlListParser()" />

				<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>
		<tr>
			<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"></td>
			<td><label for="checkTodos">Todos</label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{orientacaoAcademica.buscar}" id="btnbscar"/>
				<h:commandButton value="Cancelar" action="#{orientacaoAcademica.cancelar}" onclick="#{confirm}" immediate="true" id="btaoCancelOp"/>
			</td>
		</tr>
	</tfoot>
	</h:form>
	</table>

	<br/>
	</c:if>

	<c:if test="${not empty orientacaoAcademica.lista}">
		<center>
		<div class="infoAltRem">
			<c:if test="${orientacaoAcademica.gerenciarOrientacoes}">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Remover Orientação Acadêmica
			</c:if>
				<h:graphicImage value="/img/report.png" style="overflow: visible;" />:Visualizar Histórico
				<h:graphicImage value="/img/view2.gif" style="overflow: visible;" />:Emitir Atestado de Matrícula
				<h:graphicImage value="/img/email_go.png" style="overflow: visible;" />:Enviar E-mail
				<c:if test="${not orientacaoAcademica.gerenciarOrientacoes}">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />:Visualizar Orientações Dadas
				</c:if>
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />:Integralizações do Currículo
		</div>
		</center>

		<table class=listagem>
			<caption class="listagem">Lista de Orientações Acadêmicas (${fn:length(orientacaoAcademica.lista)})</caption>
			<thead>
				<tr>
					<td>Status do Discente</td>
					<c:if test="${orientacaoAcademica.gerenciarOrientacoes}">
					<td>
						Orientador
					</td>
					</c:if>
					<td>Discente</td>
					<td>E-mail</td>
					<td>Início da Orientação</td>
					<td></td>
					<td></td>
					<td colspan="3"></td>
					<c:if test="${not orientacaoAcademica.gerenciarOrientacoes}">
						<td></td>
					</c:if>
				</tr>
			</thead>

			<c:set var="nivelOrientado" />
			<c:forEach items="${orientacaoAcademica.lista}" var="item" varStatus="status">
				<c:set var="stripe" value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>

				<%-- Nível dos orientandos --%>
				<c:if test="${ item.discente.nivel != nivelOrientando }">
					<c:set var="nivelOrientando" value="${item.discente.nivel}"/>
					<tr>
						<td class="subFormulario" colspan="10"> ${item.discente.nivelDesc} </td>
					</tr>
				</c:if>

				<tr class="${stripe}">
					
					<td style="width:10%;${item.discente.cancelado?'color:red;':''}">
						${item.discente.statusString}
					</td>	
						
					<c:if test="${orientacaoAcademica.gerenciarOrientacoes}">
						<td>${item.nomeOrientador}</td>
					</c:if>
					
					<td>${item.discente}</td>

					<td><a href="mailto:${item.discente.pessoa.email}">${item.discente.pessoa.email}</a></td>
					
					<td>
						<fmt:parseDate var="dataFormatada" value="${item.inicio}" pattern="yyyy-MM-dd" type="date"/>
						<fmt:formatDate value="${dataFormatada}" pattern="dd/MM/yyyy"/>
					</td>
					
					<td width="1%">
						<c:if test="${orientacaoAcademica.gerenciarOrientacoes}">
						<h:form>
							<input type="hidden" value="${item.id}" name="id" />
							<h:commandButton image="/img/delete.gif" styleClass="noborder" alt="Remover" title="Remover Orientação Acadêmica" id="btaoRemOrientAcademica"
							action="#{orientacaoAcademica.removerOrientacao}" onclick="#{confirmDelete}"/>
						</h:form>
						</c:if>
					</td>
					
					<td  width="1%">
					<h:form>
						<input type="hidden" value="${item.discente.id}" name="id" />
						<h:commandButton image="/img/report.png" styleClass="noborder" alt="Visualizar Histórico" title="Visualizar Histórico" id="btnVerHistoricos"
						action="#{historico.selecionaDiscenteForm}"/>
					</h:form>
					</td>
					
					<td  width="1%">
					<h:form>
						<input type="hidden" value="${item.discente.id}" name="id" />
						<h:commandButton image="/img/view2.gif" styleClass="noborder" alt="Atestado de Matrícula" title="Emitir Atestado de Matrícula" id="btnatestadosMatriculas"
						action="#{atestadoMatricula.selecionaDiscenteForm}"/>
					</h:form>
					</td>
					
					<td  width="1%">
					<h:form>
						<input type="hidden" value="${item.discente.id}" name="idDiscente" />
						<input type="hidden" value="${item.id}" name="idOrientacao" />
						<h:commandButton image="/img/email_go.png" alt="Enviar E-mail" title="Enviar E-mail" id="btaoDeEnviarEmails"
						action="#{orientacaoAcademica.redirecionar}"/>
					</h:form>
					</td>
					
					<c:if test="${not orientacaoAcademica.gerenciarOrientacoes}">
						<td width="1%">
							<h:form>
								<input type="hidden" value="${item.discente.id}" name="idDiscente" id="idDoDiscente"/>
								<input type="hidden" value="${item.discente.nivel}" name="nivelDiscente" id="nivelDoDiscente"/>
								<h:commandButton image="/img/view.gif" alt="Visualizar Orientações Dadas" title="Visualizar Orientações Dadas"
								action="#{orientacaoAcademica.visualizarOrientacoesDadas}" id="verOrientacoesSubmeidas"/>
							</h:form>
						</td>
					</c:if>		
												
					<td  width="1%">
					<h:form>
						<input type="hidden" value="${item.discente.id}" name="idDiscente" />
						<h:commandButton image="/img/listar.gif" alt="Integralizações do Currículo" title="Integralizações do Currículo" id="btaoIntCurriculo"
						action="#{relatorioIntegralizacaoCurriculoMBean.selecionaDiscente}"/>
					</h:form>
					</td>		
									
				</tr>
			</c:forEach>
			<tr>	
				<td class="subFormulario" colspan="10"> Lista de E-mail do Grupo de Orientandos<ufrn:help>Caso deseje enviar um e-mail para todos os discentes que orienta ou enviar um arquivo por e-mail, copie esta lista de e-mails e cole no programa de envio de e-mail que utiliza.</ufrn:help></td>
			</tr>
			<tr>	
				<td colspan="10">${orientacaoAcademica.emailDiscentes}</td>
			</tr>
			<c:if test="${!orientacaoAcademica.gerenciarOrientacoes}">
				<tr>
					<h:form>
						<td class="subFormulario" colspan="10">
							 Últimas Mensagens Enviadas
							<span style="float: right;"><h:commandLink style="font-variant: normal;" value="Listar Todas" action="#{orientacaoAcademica.listarMensagens }"/></span>
						</td>
					</h:form>
				</tr>
				<tr>
					<c:if test="${empty mensagemOrientacao.ultimasMensagens}">
						<td colspan="10"><i>Nenhuma mensagem encontrada.</i></td>
					</c:if>
					<c:if test="${not empty mensagemOrientacao.ultimasMensagens}">
						<td style="text-align: center;"><b>Matrícula</b></td>
						<td colspan="3"><b>Discente</b></td>
						<td colspan="6" style="text-align: center;"><b>Data de envio</b></td>
						
						<c:forEach items="${mensagemOrientacao.ultimasMensagens}" var="mensagem" varStatus="status" >
						<c:set var="stripe" value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>
							<tr class="${stripe}">
								<td style="text-align: center;">
									${mensagem.orientacaoAcademica.discente.matricula }
								</td>
								<td colspan="3" width="80%">
									${mensagem.orientacaoAcademica.discente.pessoa.nome }
								</td>
								<td colspan="6" style="text-align: center;" nowrap="nowrap">
									<ufrn:format type="data" valor="${mensagem.dataCadastro }" />
								</td>
							</tr>
							<tr class="${stripe}">
								<td colspan="10" style="padding: 1% 3% 1% 3%;"><em>${mensagem.mensagem }</em></td>
							</tr>
						</c:forEach>
					</c:if>
				</tr>
			</c:if>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
