<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Dados do Corpo Docente do Curso</h2>
	
	<%@include file="include/_operacao.jsp"%>
	
  <table class=formulario width="100%">
   <caption class="listagem">Buscar Docente</caption>
	 <tr>
		<td colspan="4">
	       <div id="abas-docente">
  			  <div id="interno" class="aba">
				<h:form id="form1">
				<table class="subFormulario" width="100%">
				  <caption>Docente Interno</caption>
					<tr>
						<th class="obrigatorio">Docente:</th>
						<td>
							<h:inputText value="#{cursoLatoMBean.corpoDocenteCursoLato.servidor.pessoa.nome}" id="nomeServ" size="59"/>
							<rich:suggestionbox for="nomeServ" width="450" height="100" minChars="3" id="suggestionNomeServ" 
								suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_serv" 
								fetchValue="#{_serv.pessoa.nome}">
							 
								<h:column>
									<h:outputText value="#{_serv.pessoa.nome}" />
								</h:column>
							 
						        <f:param name="apenasAtivos" value="true" />
						        <a4j:support event="onselect">
							        <f:param name="apenasAtivos" value="true" />
									<f:setPropertyActionListener value="#{_serv.id}" target="#{cursoLatoMBean.corpoDocenteCursoLato.servidor.id}" />
							    </a4j:support>
							</rich:suggestionbox>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="5"><h:commandButton value="Adicionar " action="#{cursoLatoMBean.adicionarServidor}" id="adicionarServidor" /></td>
					</tr>
				</table>
				</h:form>
			  </div>
			  <div id="externo" class="aba">
			   <h:form id="form2">
			   <table class="subFormulario" width="100%" id="tableExterno">
			   		<tr>
						<th>Estrangeiro:</th>
			   			<td>
							<h:selectOneRadio value="#{cursoLatoMBean.estrangeiro}" id="estrangeiro" 
										valueChangeListener="#{cursoLatoMBean.changeEstrangeiro}">
								<f:selectItems value="#{cursoLatoMBean.simNao}"/>
								<a4j:support event="onclick" reRender="form2" />
							</h:selectOneRadio>
			   			</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">
				   			<h:outputText id="cpf" rendered="#{!cursoLatoMBean.estrangeiro}">CPF:</h:outputText>
				   			<h:outputText id="passaporte" rendered="#{cursoLatoMBean.estrangeiro}" >Passaporte:</h:outputText>
			   			</th>
						<td>
							<h:inputText value="#{ cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.cpf_cnpj }" size="14" maxlength="14"
      							onkeypress="return formataCPF(this, event, null)" id="txtCPF" disabled="#{cursoLatoMBean.readOnly}" 
      							rendered="#{!cursoLatoMBean.estrangeiro}">
          						<f:converter converterId="convertCpf"/>
          						<f:param id="paraCpf" name ="type" value="cpf" />
          						<a4j:support action="#{cursoLatoMBean.carregaDocenteExterno}" event="onchange" 
          							reRender="txtCPF, nomeDocenteExterno, nomeMaeDocenteExterno, emailDocenteExterno, sexo" />
     						</h:inputText>
							<h:inputText value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.passaporte}" 
								size="22" maxlength="20" id="passaporteDocenteExterno" rendered="#{cursoLatoMBean.estrangeiro}" >
       						<a4j:support action="#{cursoLatoMBean.carregaDocenteExterno}" event="onchange" 
       							reRender="txtCPF, nomeDocenteExterno, nomeMaeDocenteExterno, emailDocenteExterno, sexo" rendered="#{cursoLatoMBean.estrangeiro}"/>
							</h:inputText>
			   			</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">Nome:</th>
						<td>
							<h:inputText value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.nome}" size="70" 
							maxlength="100" id="nomeDocenteExterno" onkeyup="CAPS(this);" disabled="#{cursoLatoMBean.obj.cpfEncontrado}"/>
						</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">Nome da Mãe:</th>
						<td>
							<h:inputText value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.nomeMae}" size="70" 
							maxlength="100" id="nomeMaeDocenteExterno" />
						</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">Email:</th>
			   			<td>
							<h:inputText value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.email}" size="70" 
							maxlength="100" id="emailDocenteExterno" />
						</td>
			   		</tr>
			   		<tr>
						<th class="obrigatorio">Sexo:</th>
						<td>
							<h:selectOneRadio value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.pessoa.sexo}" id="sexo">
								<f:selectItems value="#{cursoLatoMBean.mascFem}" />
							</h:selectOneRadio>
			   			</td>
			   		</tr>
			   		<tr>
						<th>Técnico da ${ configSistema['siglaInstituicao'] }:</th>
			   			<td>
							<h:selectOneRadio value="#{cursoLatoMBean.tecnico}" id="tecnico" readonly="#{cursoLatoMBean.obj.cpfEncontrado}">
								<f:selectItems value="#{cursoLatoMBean.simNao}"/>
							</h:selectOneRadio>
			   			</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">Formação:</th>
			   			<td>
							<h:selectOneMenu value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.formacao.id}" id="formacao" >
								<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
								<f:selectItems value="#{formacao.allCombo}"/>
							</h:selectOneMenu>
			   			</td>
			   		</tr>
			   		<tr>
			   			<th class="obrigatorio">Instituição:</th>
			   			<td>

							<h:selectOneMenu value="#{cursoLatoMBean.corpoDocenteCursoLato.docenteExterno.instituicao.id}" id="instituicao" >
								<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
								<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
							</h:selectOneMenu>
			   			</td>
			   		</tr>
			   		<tr>
			   			<td align="center" colspan="5">
			   				<h:commandButton value="Adicionar" action="#{cursoLatoMBean.adicionarDocenteExterno}" id="adicionarExterno" />
			   			</td>
			   		</tr>
			   </table>
			   </h:form>
    		   </div>
		   </div>
	    </td>
     </tr>		 
   </table>
   

	<div class="infoAltRem">
    	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Docente
    	<html:img page="/img/listar.gif" style="overflow: visible;"/>: Visualizar Curriculo Lattes
	</div>
	<h:form id="form">
	 <table class="listagem" width="80%">
		<caption class="listagem">Corpo Docente do Curso</caption>
	        <thead>
		        <tr>
		        	<td>Siape</td>
			        <td>Nome</td>
			        <td>Titulação</td>
			        <td>Instituição</td>
			        <td colspan="2"></td>
			    </tr>
	        </thead>
	        <a4j:region id="corpoDocente">
		        <c:forEach items="#{cursoLatoMBean.corpoDocenteProposta}" var="cursoServidor" varStatus="status">
		            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	 		            <c:choose>
			            	<c:when test="${ not empty cursoServidor.docenteExterno.id }">
								<td> - </td>
			                    <td>${ cursoServidor.docenteExterno.pessoa.nome }</td>
			                    <td>${ cursoServidor.docenteExterno.formacao.denominacao }</td>
			                    <td>${ cursoServidor.docenteExterno.instituicao.sigla }</td>
		   						<td width="20">
									<h:commandLink action="#{ cursoLatoMBean.removerDocente }" onclick="#{ confirmDelete }">
									 	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Docente"/>
										<f:param name="idDocente" value="#{ cursoServidor.id }"/>
									</h:commandLink>
								</td>
		   						<td>
			   						<c:if test="${not empty cursoServidor.linkCurriculoLattes}">
			   							 <a href="${cursoServidor.linkCurriculoLattes}" target="_blank"> 
			   							 	<img src="${ctx}/img/listar.gif">
			   							 </a>
			   						</c:if>
		   						</td>
			                </c:when>
			                <c:otherwise>
			                	<td>${cursoServidor.servidor.siape}</td>
			                    <td>${cursoServidor.servidor.pessoa.nome}</td>
			                    <td>${cursoServidor.servidor.formacao.denominacao}</td>
			                    <td>${ configSistema['siglaInstituicao'] }</td>
		   						<td width="20">
									<h:commandLink action="#{cursoLatoMBean.removerDocente}" onclick="#{confirmDelete}">
									 	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Docente"/>
										<f:param name="idDocente" value="#{cursoServidor.id}"/>
									</h:commandLink>
								</td>
	   							<td>
			   						<c:if test="${not empty cursoServidor.linkCurriculoLattes}">
			   							 <a href="${cursoServidor.linkCurriculoLattes}" target="_blank"> 
			   							 	<img src="${ctx}/img/listar.gif">
			   							 </a>
				   					</c:if>
		   						</td>
			                </c:otherwise>
			            </c:choose>
		            </tr>
		        </c:forEach>
	        </a4j:region>
			  <tfoot>
				   <tr>
						<td colspan="6" align="center">
							<h:commandButton value="<< Voltar" action="#{cursoLatoMBean.telaAnterior}" id="voltar" />
							<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
							<h:commandButton value="Avançar >>" action="#{cursoLatoMBean.cadastrar}" id="cadastrar" />
						</td>
				   </tr>
			  </tfoot>
   		</table>

<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-docente');
	        abas.addTab('interno', "DOCENTE CADASTRADO (${configSistema['siglaInstituicao']})");
	        abas.addTab('externo', "CADASTRAR NOVO DOCENTE EXTERNO");

	        <c:if test="${cursoLatoMBean.obj.aba == ''}">
				abas.activate('interno');
			</c:if>
	    	<c:if test="${cursoLatoMBean.obj.aba != ''}">
    			abas.activate('${cursoLatoMBean.obj.aba}');
	    	</c:if>
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

</h:form>	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>