<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h2><ufrn:subSistema /> &gt; Cadastro de Grupos de Pesquisa</h2>
	
<h:form id="form">
	<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
	<table class="formulario" width="75%">
        <caption class="listagem">Dados do Grupo de Pesquisa</caption>
        <h:inputHidden value="#{grupoPesquisa.confirmButton}" />
		<h:inputHidden value="#{grupoPesquisa.obj.id}" />
        <tbody>
        <tr>
            <th class="obrigatorio">Código:</th>
            <td>
                <h:inputText id="codigo" value="#{grupoPesquisa.obj.codigo}"
                	size="20" maxlength="20"/>
            </td>
        </tr>
        <tr>
            <th class="obrigatorio">Nome:</th>
            <td>
                <h:inputText id="nome" value="#{grupoPesquisa.obj.nome}"
                	size="80" maxlength="255"/>
            </td>
        </tr>
		<tr>
			<th class="obrigatorio">Líder:</th>
			<td>
				<h:inputHidden id="idCoordenador" value="#{grupoPesquisa.obj.coordenador.id}"/>
				<h:inputText id="nomeCoordenador" value="#{grupoPesquisa.obj.coordenador.pessoa.nome}" size="70" onkeyup="CAPS(this);" />
				<ajax:autocomplete source="form:nomeCoordenador" target="form:idCoordenador"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicatorDocente" style="display:none; "> 
				<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
				</span>
			</td>
		</tr>
        <tr>
            <th class="obrigatorio">Status:</th>
            <td>
               <h:selectOneMenu id="status"
					value="#{grupoPesquisa.obj.status}" style="width: 70%;">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM STATUS --"/>
					<f:selectItems value="#{grupoPesquisa.tiposStatusCombo}"/>
				</h:selectOneMenu>
            </td>
        </tr>
        <c:if test="${grupoPesquisa.obj.id > 0}">
	        <tr>
	            <th>Área Atual:</th>
	            <td>
	            	<c:if test="${empty grupoPesquisa.obj.areaConhecimentoCnpq or empty grupoPesquisa.obj.areaConhecimentoCnpq.nome}">
	            		<font color="red"> Não definida</font>
	            	</c:if>
	            	<c:if test="${not empty grupoPesquisa.obj.areaConhecimentoCnpq}">
		            	<h:outputText id="areaCNPQAtual" value="#{grupoPesquisa.obj.areaConhecimentoCnpq.nome}" />
	            	</c:if>
	            </td>
	        </tr>
        </c:if>
        <tr>
            <th class="obrigatorio">Área de Conhecimento:</th>
            <td>
               <h:selectOneMenu id="areaCNPQ"
					value="#{grupoPesquisa.obj.areaConhecimentoCnpq.id}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA ÁREA DO CNPq --"/>
					<f:selectItems value="#{area.allGrandesAreasCombo}"/>
				</h:selectOneMenu>
            </td>
        </tr>
        <tr>
            <th>Home Page:</th>
            <td>
                <h:inputText id="homePage" value="#{grupoPesquisa.obj.homePage}"
                	size="80" readonly="#{grupoPesquisa.readOnly}"/>
            </td>
        </tr>
        <tr>
            <th>E-Mail:</th>
            <td>
                <h:inputText id="email" value="#{grupoPesquisa.obj.email}"
                	size="80" readonly="#{grupoPesquisa.readOnly}"/>
            </td>
        </tr>
        <tr>
            <th>Ativo:</th>
            <td>
                <h:selectOneRadio id="ativo" value="#{grupoPesquisa.obj.ativo}" readonly="#{grupoPesquisa.readOnly}">
                	<f:selectItems value="#{grupoPesquisa.simNao}"/>
                </h:selectOneRadio>
            </td>
        </tr>
        
        <tr>
        	<td colspan="2" class="subFormulario">Membros do Grupo de Pesquisa</td>
        </tr>
        
        <tr>
			<td colspan="2" class="teste">
				<p style="text-align: center; font-style: italic; padding: 5px;">
					Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos
				</p>
				<div id="tabs-membro">
					<div id="membro-docente">
						<table width="100%">
							<tr>
								<th width="15%" class="required">Docente:</th>
								<td>
									<h:inputHidden id="idDocente" value="#{grupoPesquisa.docente.id}"></h:inputHidden>
									<h:inputText id="nomeDocente" value="#{grupoPesquisa.docente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}" onkeyup="CAPS(this);"/>

									 <ajax:autocomplete
										source="form:nomeDocente" target="form:idDocente"
										baseUrl="/sigaa/ajaxDocente" className="autocomplete"
										indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,inativos=true"
										parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
						</table>
					</div>

					<div id="membro-servidor">
						<table width="100%">
							<tr>
								<th width="15%" class="required">Servidor:</th>
								<td>
									<h:inputHidden id="idServidor" value="#{grupoPesquisa.servidor.id}"></h:inputHidden>
									<h:inputText id="nomeServidor"	value="#{grupoPesquisa.servidor.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}" onkeyup="CAPS(this);"/>

									 <ajax:autocomplete
										source="form:nomeServidor" target="form:idServidor"
										baseUrl="/sigaa/ajaxServidor" className="autocomplete"
										indicator="indicator" minimumCharacters="3" parameters="tipo=todos,situacao=ativo"
										parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>

								</td>
							</tr>
						</table>
					</div>


					<div id="membro-discente">
						<table width="100%">
							<tr>
								<th width="15%" class="required">Discente:</th>
								<td>
									<h:inputHidden id="idDiscente" value="#{grupoPesquisa.discente.id}"></h:inputHidden>
									<h:inputText id="nomeDiscente"	value="#{grupoPesquisa.discente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}" onkeyup="CAPS(this);"/>

									 <ajax:autocomplete
										source="form:nomeDiscente" target="form:idDiscente"
										baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
										indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=E,D"
										parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorDiscente"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</td>
		</tr>

		<tr>
			<th width="20%"  class="required">Classificação:</th>
			<td>
				<h:selectOneMenu value="#{grupoPesquisa.membroEquipe.classificacao}" id="membroClassificacao">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA CLASSIFICAÇÃO --"/>
					<f:selectItem itemValue="1" itemLabel="Líder"/>
					<f:selectItem itemValue="2" itemLabel="Membro"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th width="15%" class="required">Tipo:</th>
			<td>
				<h:selectOneMenu id="tipoMembroEquipe"
					value="#{grupoPesquisa.membroEquipe.tipoMembroGrupoPesquisa.id}">

					<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --"/>
					<f:selectItems value="#{tipoMembroGrupoPesquisa.allCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th class="required">Data Início:</th>
			<td>
				<t:inputCalendar popupDateFormat="#{grupoPesquisa.padraoData}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{grupoPesquisa.membroEquipe.dataInicio}" id="dataInicio"  />
			</td>
		</tr>

		<tr>
			<th>Data Fim:</th>
			<td>
				<t:inputCalendar popupDateFormat="#{grupoPesquisa.padraoData}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{grupoPesquisa.membroEquipe.dataFim}" id="dataFim"  />
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center">
				<h:panelGroup id="botoes">
					<h:commandButton id="btnAdicionarMembro" value="Adicionar" action="#{grupoPesquisa.adicionarMembroEquipe}" rendered="#{!grupoPesquisa.alterar}" />
					<h:commandButton id="btnAlterarMembro" value="Alterar Membro" action="#{grupoPesquisa.adicionarMembroEquipe}" rendered="#{grupoPesquisa.alterar}" />
					<h:commandButton id="btnCancelarAlteracao" value="Cancelar Alteração de Membro" actionListener="#{grupoPesquisa.cancelarAlterarMembroEquipe}" rendered="#{grupoPesquisa.alterar}"/>
				</h:panelGroup>
			</td>
		</tr>
		
		<c:if test="${not empty grupoPesquisa.obj.equipesGrupoPesquisa}">
			<tr>
				<td colspan="2" class="subFormulario">
					Lista de Membros do Grupo de Pesquisa
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<input type="hidden" name="idMembro" value="0" id="idMembro"/>
					<input type="hidden" name="posicao" value="-1" id="posicao"/>
					
					<div class="infoAltRem">
							<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				        	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
					</div>
					
					<t:dataTable id="dt_membro_equipe" value="#{grupoPesquisa.membrosGrupoPesquisa}" var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="indice">
						<t:column>
							<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
							<h:outputText value="#{membro.servidor.pessoa.nome}" rendered="#{empty membro.pessoa && membro.categoriaMembro.id == 1}" id="nome_docente" />
							<h:outputText value="#{membro.pessoa.nome}" rendered="#{not empty membro.pessoa}" id="nome_pessoa" />
						</t:column>
						<t:column>
							<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
							<h:outputText value="#{membro.categoriaMembro.descricao}" rendered="#{not empty membro.categoriaMembro}" id="categoria" />
						</t:column>
						<t:column>
							<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
							<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							<h:outputText value="#{membro.classificacaoString}" />
							<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
						</t:column>
						<t:column width="30%">
							<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
							<h:outputText value="#{membro.tipoMembroGrupoPesquisa.descricao}" rendered="#{not empty membro.tipoMembroGrupoPesquisa}" />
						</t:column>
						
						<t:column width="5%" styleClass="centerAlign">
							<h:commandButton image="/img/delete.gif" actionListener="#{grupoPesquisa.removeMembroEquipe}" id="bt_remove"
								alt="Remover membro" title="Remover membro"  
								onclick="$(idMembro).value=#{membro.id};$(posicao).value=#{indice};return confirm('Deseja Remover este Membro do Grupo de Pesquisa?')"/>
						</t:column>
						<t:column width="5%" styleClass="centerAlign">
							<h:commandLink actionListener="#{grupoPesquisa.popularMembroEquipe}" id="bt_alterar" rendered="#{membro.id > 0}"
									title="Alterar membro">
                    			<f:param name="id" value="#{membro.id}"/>
                    			<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</t:column>	
					</t:dataTable>

				</td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnConfirm" value="#{grupoPesquisa.confirmButton}"	action="#{grupoPesquisa.cadastrar}" />
					<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{grupoPesquisa.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-membro');
	        tabs.addTab('membro-docente', "Docente")
	        tabs.addTab('membro-servidor', "Servidor Técnico-Administrativo");
	        tabs.addTab('membro-discente', "Discente");
			//tabs.addTab('membro-externo', "Docente Externo");

  		        tabs.activate('membro-docente');	////padrão

  		      <c:if test="${sessionScope.aba != null}">
			    	tabs.activate('${sessionScope.aba}');
		 </c:if>

	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	
	var verificaEstrangeiro = function() {
		if ( $('equipe:checkEstrangeiro').checked ) {
			$('equipe:cpfExterno').disable();
			$('equipe:nomeExterno').enable();
			$('equipe:sexoExterno').enable();
			$('equipe:cpfExterno').value = "";
			$('equipe:nomeExterno').value = "";
		} else {
			$('equipe:cpfExterno').enable();
			$('equipe:nomeExterno').disable();
			$('equipe:sexoExterno').disable();
		}
	}
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>