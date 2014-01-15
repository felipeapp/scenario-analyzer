<%@include file="/ensino/notificacoes_academicas/cabecalho.jsp"%>

<h2> Notifica��es quanto ao preenchimento do question�rio de extens�o </h2>

<jwr:style src="/css/ensino/notificacoes.css" media="all" />

<style>
div.logon h3{ font-size: 12px !important;padding-right:15px;}
.confirmaSenha { float: left !important; }
</style>

<f:view>
	<h:form>

	<img src="${ctx}/img/notificacao/imagemNotificacoes.png" style="padding-left:20px;float:left"/>
	
	<div class="intro">
		<div class="textos">
			<p style="text-align: justify;">
				Prezado usu�rio foi designado para o senhor realizar o preenchimento do(s) seguintes question�rios. <br/><br/>
	
				Na certeza do atendimento � solicita��o, agradecemos antecipadamente o empenho de Vossa Senhoria no 
				desenvolvimento de atividades acad�micas no campo da Extens�o Universit�ria.<br/><br/> 
	
				Atenciosamente, <br/><br/>
	
				PROEx<br/><br/>
	
				3215-3231
			<p>
		</div>		
			
			<br clear="all"/>
	</div>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Responder Question�rio<br/>
	</div>
	
		<table class="listagem">
		  <caption class="listagem"> Question�rios Pendentes (${ fn:length(questionarioProjetoExtensaoMBean.questionarios) })</caption>
			<thead>
				<tr>
					<th width="15%">Projeto</th>
					<th width="15%">Tipo Projeto</th>
					<th width="20%">Question�rio</th>
					<th></th>					
				</tr>
				</thead>
				<tbody>
					<c:forEach items="#{questionarioProjetoExtensaoMBean.questionarios}" var="_quest" varStatus="status">						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${ _quest.projeto.titulo }</td>
							<td>${ _quest.projeto.tipoAcaoExtensao }</td>
							<td> ${ _quest.questionario.titulo } </td>
							<td width="2%" style="text-align: center;">
								<h:commandLink action="#{questionarioProjetoExtensaoMBean.iniciarRespostaQuestionario}" >
									<h:graphicImage value="/img/extensao/document_new.png" style="overflow: visible;" title="Responder Question�rio" />
									<f:param name="id" value="#{_quest.id}"/>
								</h:commandLink>
							</td>	
						</tr>
					</c:forEach>
				</tbody>
		</table>
		
		<br /><br />
		<center>
			<h:commandLink action="#{ questionarioProjetoExtensaoMBean.pularQuestionarios }">
				<h4> Pular Question�rio(s) </h4>
			</h:commandLink>
		</center>
		
	</h:form>
</f:view>	

<%@include file="/ensino/notificacoes_academicas/rodape.jsp"%>