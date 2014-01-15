<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>

<div class="secaoConfig">
<%@include file="/cv/include/_info_comunidade.jsp" %>

	<div class="colDirConf">
	<rich:panel header="Adm. da comunidade" headerClass="headerBloco">
	
			<h:form id="AdministracaoComunidade">
			<ul>
				<c:if test="${ comunidadeVirtualMBean.membro.administrador }">
				<li>&rsaquo;
					<h:commandLink style="color: #AA5555;" action="#{ comunidadeVirtualMBean.desativarComunidadeVirtual }" 
							onclick="return(confirm('Caso essa comunidade seja desativada, os participantes da mesma n�o ter�o mais acesso a comunidade. Realmente deseja DESATIVAR a comunidade?'));" value="Desativar Comunidade Virtual" id="botaoDesativarComunidade">
					</h:commandLink>  
				</li>
				<li>&nbsp;</li>
				</c:if>
				<li>&rsaquo; <a href="/sigaa/cv/ConteudoComunidade/listar.jsf"> Conte�do da comunidade </a> </li>
	      		<li>&rsaquo; <a href="/sigaa/cv/EnqueteComunidade/listar.jsf"> Enquete </a> </li>
	      		<li>&rsaquo; <a href="/sigaa/cv/ForumMensagemComunidade/listar.jsf"> F�rum </a> </li>
				<li>&rsaquo; <a href="/sigaa/cv/IndicacaoReferenciaComunidade/listar.jsf"> Indica��o de refer�ncia </a> </li>
				<li>&rsaquo; <a href="/sigaa/cv/TopicoComunidade/listar.jsf"> T�picos da comunidade </a> </li>   
				<li>&rsaquo; <a href="/sigaa/cv/MembroComunidade/participantes.jsf"> Participantes </a> </li> 
				<li>&rsaquo; <a href="/sigaa/cv/NoticiaComunidade/listar.jsf"> Not�cias </a> </li>
			</ul>
			</h:form>
	</rich:panel>
	
	</div>
	<div class="colEsqConf">
	
	<rich:panel header="SOLICITA��ES PENDENTES PARA COMUNIDADES MODERADAS" style="position:relative;width:80" headerClass="headerBloco" rendered="#{ comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.moderada }">

		<div class="infoAltRem">
			<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">
				<img src="${ctx}/img/check.png" /> : Permitir participa��o
				<img src="${ctx}/img/cross.png" /> : Recusar participa��o
		 	</c:if>
		</div>
		<br/>
		
		<c:set var="listaSolicitacoes" value="#{comunidadeVirtualMBean.listaSolicitacoes}" />
		
		<c:if test="${ empty listaSolicitacoes}">
			<p class="vazio">Nenhum item foi encontrado.</p>
		</c:if>
	
		<c:if test="${ not empty listaSolicitacoes }">
			<h:form>
				<table class="listagem">
					<thead>
						<tr>
							<th>Solicitantes</th>
							<th style="text-align:center">Data de Solicita��o</th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="solicitacao" items="#{ listaSolicitacoes }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
								
								<td class="first">${ solicitacao.usuario.pessoa.nome }</td>
								<td class="width90"><fmt:formatDate pattern="dd/MM/yyyy" value="${ solicitacao.dataSolicitacao }" /></td>
								
								<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">
									<td class="icon">
										<h:commandLink
											id="liberarUsuarioComunidade"
											action="#{ comunidadeVirtualMBean.liberarParticipacaoUsuarioComunidade }"
											styleClass="confirm-remover"
											onclick="return(confirm('Deseja realmente ACEITAR esse usu�rio na comunidade?'));">
											
											<f:param name="id" value="#{ solicitacao.id }" />
											<f:param name="idPessoa" value="#{ solicitacao.usuario.pessoa.id }" />
											<h:graphicImage alt="Remover" title="Aceitar participa��o" value="/img/check.png" />
											
										</h:commandLink>
									</td>
											
									<td class="icon">
										<h:commandLink
											id="recusarUsuarioComunidade"
											action="#{ comunidadeVirtualMBean.recusarParticipacaoUsuarioComunidade }"
											styleClass="confirm-remover"
											onclick="return(confirm('Deseja realmente RECUSAR esse usu�rio a entrar na comunidade?'));">
											
											<f:param name="id" value="#{ solicitacao.id }" />
											<f:param name="idPessoa" value="#{ solicitacao.usuario.pessoa.id }" />
											<h:graphicImage alt="Remover" title="Recusar participa��o" value="/img/cross.png" />
										</h:commandLink>
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</h:form>	
		</c:if>
	</rich:panel>
	
	<c:if test="${ comunidadeVirtualMBean.membro.administrador }">		
		<h:form id="formDadosComunidade">
			<div class="dadosComunidade">
			<rich:panel header="Dados da Comunidade" headerClass="headerBloco">
			<table class="formulario">
			<caption>Alterar Dados</caption>
			<tr>
					<th>Nome:</th>
					<td>
						<h:inputText id="nome" value="#{comunidadeVirtualMBean.comunidade.nome}" size="85" /> <br/>
					</td>
				</tr>
				<tr>
					<th>Descri��o:</th>
					<td>
						<h:inputTextarea id="descricao" value="#{comunidadeVirtualMBean.comunidade.descricao}"
							rows="5" cols="78" onkeyup="this.value = this.value.substring(0, 2000);"/>
					</td>
				</tr>
				
				<tr>
					<th>Ordem das listagens:</th>
					<td>
						<h:selectOneRadio value="#{ comunidadeVirtualMBean.comunidade.ordemCrescente }">
							<f:selectItem itemValue="true" itemLabel="Mais Antigas Primeiro" />
							<f:selectItem itemValue="false" itemLabel="Mais Recentes Primeiro" />
						</h:selectOneRadio>
					</td>
				</tr>
				
				<tr>
					<th>Publicar Comunidade:</th>
					<td>
						<h:selectBooleanCheckbox id="publicarComunidade" value="#{comunidadeVirtualMBean.comunidade.permiteVisualizacaoExterna}" /> <ufrn:help>Disponibiliza os t�picos da comunidade no Portal P�blico do SIGAA</ufrn:help> 
					</td>
				</tr>
				<tr>
					<th>Tipo da comunidade:</th>
					<td>
						<h:selectOneMenu id="tipoComunidade" value="#{ comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.id }" onchange="exibirCamposTipoComunidade();" onclick="exibirCamposTipoComunidade();">
							<f:selectItem itemValue="4" itemLabel="MODERADA" />
							<f:selectItem itemValue="1" itemLabel="PRIVADA" />
							<f:selectItem itemValue="2" itemLabel="P�BLICA" />
						</h:selectOneMenu>
						<ufrn:help>Cada tipo de comunidade tem um comportamento distinto para os usu�rios do sistema. Por exemplo, qualquer usu�rio do sistema poder� participar das comunidades p�blicas, j� para participar de uma comunidade moderada, o moderador desse grupo dever� permitir a participa��o.</ufrn:help>
					</td>
				</tr>
				<%-- <c:if test="${ comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.publica or comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.moderada }"> --%>
				<%-- 	
					<tr>
						<th><span id="labelPermitirAcessoExterno">Permitir Acesso Externo:</span></th>
						<td>
							<div id="valorPermitirAcessoExterno">
								<h:selectBooleanCheckbox id="permitirAcessoExterno" value="#{comunidadeVirtualMBean.comunidade.permiteAcessoExterno}" /> <ufrn:help>Permite que visitantes se cadastrem no sistema e participem desta comunidade</ufrn:help>
							</div> 
						</td>
					</tr>
				--%>	
				<%--</c:if>--%>
				
				<%--<c:if test="${ comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.moderada }">--%>
					<tr>
						<th><span id="labelCodigoAcessoImediato">C�digo para acesso imediato:</span></th>
						<td>
							<div id="valorCodigoAcesso">
								<h:inputText id="codigoAcesso" maxlength="20" value="#{comunidadeVirtualMBean.comunidade.codigoAcesso}" style="width:50px;" />
								<ufrn:help>Se um c�digo for informado, os usu�rios que tentarem ingressar nesta comunidade poder�o digit�-lo para obter acesso imediato. Os que n�o souberem o c�digo ir�o para a lista de solicita��es.</ufrn:help>
							</div> 
						</td>
					</tr>
					<tr>
						<th><span id="labelDicaCodigoAcessoImediato">Dica para o c�digo:</span></th>
						<td>
							<div id="valorDicaCodigoAcesso">
								<h:inputText id="dicaCodigoAcesso" style="width:400px;" maxlength="300" value="#{comunidadeVirtualMBean.comunidade.dicaCodigoAcesso}" />
								<ufrn:help>Dica que ser� exibida quando o c�digo de acesso for solicitado.</ufrn:help>
							</div> 
						</td>
					</tr>
				<%--</c:if>--%>
				<tfoot>
					<tr> 
						<td colspan="2"> 
							<h:commandButton value="Alterar Dados" action="#{comunidadeVirtualMBean.alterarDadosComunidade}" id="cadastrar"/>
							 <h:commandButton value="Cancelar" action="#{comunidadeVirtualMBean.paginaPrincipal}" onclick="return confirm('Deseja realmente cancelar a opera��o?');" id="cancelarDadosOperacao"/> 
						</td>
					</tr>
				</tfoot>
			</table>
			</rich:panel>
			</div>
		</h:form>
		
		<script>

			var labelPAE = document.getElementById("labelPermitirAcessoExterno");
			var valorPAE = document.getElementById("valorPermitirAcessoExterno");
			var labelCAI = document.getElementById("labelCodigoAcessoImediato");
			var valorCA = document.getElementById("valorCodigoAcesso");
			var labelDCAI = document.getElementById("labelDicaCodigoAcessoImediato");
			var valorDCA = document.getElementById("valorDicaCodigoAcesso");
		
			function exibirCamposTipoComunidade (){

				labelPAE.style.display = "none";
				valorPAE.style.display = "none";
				labelCAI.style.display = "none";
				valorCA.style.display = "none";
				labelDCAI.style.display = "none";
				valorDCA.style.display = "none";

				switch (document.getElementById("formDadosComunidade:tipoComunidade").value){
					/*
						PRIVADA = 1;
						PUBLICA = 2;
						RESTRITO_GRUPO = 3;
						MODERADA = 4;
					*/
					case "4":
						labelCAI.style.display = "inline";
						valorCA.style.display = "block";
						labelDCAI.style.display = "inline";
						valorDCA.style.display = "block";
					
					case "2":
						labelPAE.style.display = "inline";
						valorPAE.style.display = "inline";
				}
			}

			exibirCamposTipoComunidade ();
		</script>
	</c:if>

		<c:if test="${ comunidadeVirtualMBean.membro.administrador && comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.id == 3}">
			<rich:panel header="GRUPOS DISPON�VEIS" headerClass="headerBloco">		
					
				<div class="descricaoOperacao">
					<p> 
						Cada comunidade do tipo <b>Restrita a Grupo</b> deve possuir um grupo associado. Quando voc� associa um grupo a comunidade, 
						est� dizendo que <b>apenas</b> os usu�rios que estiverem cadastrados <b>nesse grupo</b> poder�o ser membros dessa comunidade.
						<br><br>OBS: Comunidades do tipo RESTRITA A GRUPO n�o permitem o envio de convites para usu�rios. Os pr�prios usu�rios dever�o solicitar
						sua participa��o na comunidade.   
					</p>
				</div>
					<h:form id="formImportarGrupos">
						<span>
						
							<c:set var="grupos" value="#{comunidadeVirtualMBean.gruposCombo}"/>
							<c:if test="${ not empty grupos }">
								<center>			
									<ul>
										<h:selectOneMenu value="#{comunidadeVirtualMBean.comunidade.idGrupoAssociado}" style="width: 80%;" id="grupoAssociado">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
											<f:selectItems value="#{comunidadeVirtualMBean.gruposCombo}"/>
										</h:selectOneMenu> <br><br>
										
										<c:if test="${ comunidadeVirtualMBean.comunidade.idGrupoAssociado != null}">								
											<h:commandButton action="#{ comunidadeVirtualMBean.vincularGrupoComunidade }" value="Alterar grupo para essa comunidade" id="alterarGrupoParaComunidade"/>
										</c:if>
										<c:if test="${ comunidadeVirtualMBean.comunidade.idGrupoAssociado == null}">								
											<h:commandButton action="#{ comunidadeVirtualMBean.vincularGrupoComunidade }" value="Cadastrar grupo para essa comunidade" id="cadastrarGrupoParaComunidade"/>
										</c:if>
									</ul>
								</center>
							</c:if>
						</span>
					</h:form>
			</rich:panel>
		</c:if>
		
	</div>		
</div>
</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>