<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; E-mails de Notifica��o de Servi�os das bibliotecas </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo est�o listados os e-mails usados para as notifica��es dos servi�os prestados pelas bibliotecas. </p>
    	<br/>
    	<br/>
    	<p> Os e-mails de notifica��o s�o os e-mails usados pelo sistema para enviar notifica��es � biblioteca quando os usu�rios solicitam algum dos servi�os dela. 
    	Isso se faz necess�rio porque algumas bibliotecas usam listas de e-mails exclusivas para atender determinados tipos de servi�o.</p>
    	<p> Caso a biblioteca n�o possua e-mail de notifica��o cadastrado, todas as notifica��es de todos os servi�os ser�o enviados para o pr�prio e-mail da biblioteca. </p>
    	<p>O e-mail da biblioteca � cadastrado no caso de uso de <i>Gerenciar Bibliotecas</i> da aba <i>Cadastros</i>.</p>
    	<br/>
		<p> <strong>Observa��o:</strong> Podem ser cadastrados mais de um e-mail para cada servi�o de cada biblioteca, separando-os com ponto e v�rgula <strong>";"</strong>. </p>
	</div>
	
	<a4j:keepAlive beanName="emailsNotificacaoServicosBibliotecaMBean" />
	
	<h:form id="formListagemEmaisNotificacao">

		<div class="infoAltRem" style="width:100%;">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Visualizar/Alterar E-mails de Notifica��o
		</div> 




		<table class="listagem" style="width: 100%;">
			
			<caption class="listagem">E-mails de Notifica��o (${emailsNotificacaoServicosBibliotecaMBean.size})</caption>
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
					<th style="width: 30%;">Tipo de Servi�o</th>
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