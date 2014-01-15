<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
	
	<a4j:keepAlive beanName="grupoDiscentes" />
	<h:form id="formAva" >
		
		<fieldset>
		
			<c:if test="${ empty grupoDiscentes.grupoAluno }">
				<p class="empty-listing">Caro aluno, você ainda não foi cadastrado em nenhum grupo.</p>
			</c:if>	
			
			<c:if test="${ not empty grupoDiscentes.grupoAluno }">
			
				<div class="descricaoOperacao">
					<b>Caro discente,</b><br/><br/>
					Nesta tela é possível visualizar seu grupo de estudos vinculados a esta disciplina,
					enviar mensagens para os integrantes do grupo, ou mudar o nome do grupo caso permitido pelo docente.<br/><br/>
					O grupo de estudos é designado pelo docente da turma e tem como objetivo aumentar a comunicação entre os alunos, 
					assim como a realização de tarefas em grupos. Caso seja necessário adicionar ou remover algum integrante do grupo
					entre em contato com o docente.		
				</div>
				
				<c:if test="${ grupoDiscentes.permiteAlunoModificarNomeGrupo }">
					<div class="infoAltRem">
						<img src="/sigaa/ava/img/group_edit.png">: Alterar Nome do Grupo
					</div>
				</c:if>
			
				<legend>Grupo</legend>
				<h:inputHidden value="#{ grupoDiscentes.grupoAluno.nome }" id="idNomeGrupo" />
				<a4j:outputPanel id="grupo">
				<table class="formAva" >
					<tr>
						<th id="tituloTh" style="font-weight:bold;" width="30%">Nome do Grupo:</th>
						<td>
							<h:outputText value="#{ grupoDiscentes.grupoAluno.nome }" />
							<c:if test="${ grupoDiscentes.permiteAlunoModificarNomeGrupo }">
								<a4j:commandLink  oncomplete="document.getElementById('bVisualizarInformacoesGrupo').onclick();" title="Alterar Nome do Grupo">
									<h:graphicImage value="/ava/img/group_edit.png" style="vertical-align:middle;margin-left:5px;"/>
								</a4j:commandLink>
							</c:if>		
						</td>
					</tr>
					<tr>
						<th id="numeroTh" style="font-weight:bold;" width="30%">Número de Participantes:</th>
						<td>${ fn:length(grupoDiscentes.grupoAluno.discentes)}</td>
					</tr>
				</table>
				</a4j:outputPanel>
			</c:if>	
		
		</fieldset>
		<fieldset>
		<br/>
		
			<c:if test="${ not empty grupoDiscentes.grupoAluno }">
				<table class="participantes">
					<c:forEach items="#{ grupoDiscentes.grupoAluno.discentes }" var="d" varStatus="loop">
						<c:if test="${loop.index % 2 == 0 }">
						<tr class="${loop.index % 4 == 0 ? 'odd' : 'even' }">
						</c:if>
						
						<td width="47">
						<c:if test="${d.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${d.idFoto}&key=${ sf:generateArquivoKey(d.idFoto) }" width="48" height="60"/></c:if>
						<c:if test="${d.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
						</td>
						
						<td valign="top">
							<c:if test="${d.usuario.online}">
								<img src="${ctx}/img/portal_turma/online.png" title="Usuário On-Line no SIGAA"/>
							</c:if>
							<c:if test="${!d.usuario.online}">
								<img src="${ctx}/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA"/>
							</c:if>
							<strong>
								${d.pessoa.nome}
											
							</strong><br/>
								Curso: <em>${d.curso.descricao} </em><br/>
								Matrícula: <em>${d.matricula}</em> <br/>
								Usuário: <em>${ d.usuario != null ? d.usuario.login : "Sem cadastro no sistema" } </em><br/>
								E-mail: <em>${ d.pessoa.email != null ? d.pessoa.email : "Desconhecido" }</em>
			
						</td>
						<td width="20">
								<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ d.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
						</td>
					</c:forEach>
				</table>
			</c:if>	
		
			<input type="submit" id="bVisualizarInformacoesGrupo" style="display:none;" onclick="dialogInfoGrupo.show();return false;"/>
			
			<%-- Painel das informações sobre os exemplares. --%>
			<p:dialog id="panelGrupo" styleClass="panelPerfil" header="ALTERAR NOME DO GRUPO " widgetVar="dialogInfoGrupo" modal="true" resizable="false" width="500" height="130">
					<br/>
					<ul class="form">
						<li>
							<label><h:outputText value="Nome" />:</label>
							<h:inputText id="nome" onblur="processObjects();" value="#{grupoDiscentes.grupoAluno.nome}" size="50" maxlength="55"/>
						</li>
					</ul>
					<br/>
					<div align="center">
						<a4j:commandButton  value="Alterar" actionListener="#{grupoDiscentes.alterar}" onclick="dialogInfoGrupo.hide();"  reRender="grupo"/> 
					</div>
			</p:dialog>								
		
		</fieldset>		
	</h:form>

</f:view>

<script>
function processObjects() {
	var nome = document.getElementById('formAva:nome');
	var nomeSelecionado = document.getElementById('formAva:idNomeGrupo');
	nomeSelecionado.value = nome.value;
}
</script>

<%@include file="/ava/rodape.jsp"%>