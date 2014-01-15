<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; E-mails de Notificação de Serviços das bibliotecas </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listados os e-mails usados para as notificações dos serviços prestados pelas bibliotecas. </p>
    	<br/>
    	<br/>
    	<p> Os e-mails de notificação são os e-mails usados pelo sistema para enviar notificações à biblioteca quando os usuários solicitam algum dos serviços dela. 
    	Isso se faz necessário porque algumas bibliotecas usam listas de e-mails exclusivas para atender determinados tipos de serviço.</p>
    	<p> Caso a biblioteca não possua e-mail de notificação cadastrado, todas as notificações de todos os serviços serão enviados para o próprio e-mail da biblioteca. </p>
    	<p>O e-mail da biblioteca é cadastrado no caso de uso de <i>Gerenciar Bibliotecas</i> da aba <i>Cadastros</i>.</p>
    	<br/>
		<p> <strong>Observação:</strong> Podem ser cadastrados mais de um e-mail para cada serviço de cada biblioteca, separando-os com ponto e vírgula <strong>";"</strong>. </p>
	</div>
	
	<a4j:keepAlive beanName="emailsNotificacaoServicosBibliotecaMBean" />
	
	<h:form id="formListagemEmaisNotificacao">

		<div class="infoAltRem" style="width:100%;">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Visualizar/Alterar E-mails de Notificação
		</div> 




		<table class="listagem" style="width: 100%;">
			
			<caption class="listagem">E-mails de Notificação (${emailsNotificacaoServicosBibliotecaMBean.size})</caption>
			<tr>
				<td colspan="4">
					<table class="subFormulario" style="width: 100%; margin-top: 10px; margin-bottom: 10px;">
						<caption class="listagem">Filtros</caption>
						<tr>
							<th>Biblioteca:</th>
							<td colspan="3" style="text-align: center;">
								<h:selectOneMenu id="comboboxBiblioteca" value="#{emailsNotificacaoServicosBibliotecaMBean.idBibliotecaFiltragem}"
										valueChangeListener="#{emailsNotificacaoServicosBibliotecaMBean.filtarResultadosPorBiblioteca}" onchange="submit();">
									<f:selectItem itemLabel=" -- TODAS -- " itemValue="-1"/>
									<f:selectItems value="#{bibliotecaMBean.allCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
		</table>
		
		<table class="listagem" style="width: 100%;">
			
			<thead>
				<tr>
					<th style="width: 30%;">Biblioteca</th>
					<th style="width: 30%;">Tipo de Serviço</th>
					<th style="width: 39%;">E-mails</th>
					<th style="width: 1%;"></th>
					
				</tr>
			</thead>
			
			
			
			<c:forEach items="#{emailsNotificacaoServicosBibliotecaMBean.all}" var="emailNotificacao" varStatus="status">
				
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${emailNotificacao.biblioteca.descricao}</td>
					
					<td>${emailNotificacao.tipoServico.descricao}</td>
					
					<td>${emailNotificacao.emailsNotificacaoResumido}</td>
					
					<td>
						<h:commandLink action="#{emailsNotificacaoServicosBibliotecaMBean.preAtualizar}" title="Alterar">
							<f:param name="idBilioteca" value="#{emailNotificacao.biblioteca.id}" />
							<f:param name="valorTipoServico" value="#{emailNotificacao.tipoServico.valor}" />
							<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
						</h:commandLink>
					</td>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{emailsNotificacaoServicosBibliotecaMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>