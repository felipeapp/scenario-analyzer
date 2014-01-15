<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<c:set var="produtividade" value="${bolsaObtida.produtividade}"/>

	<c:if test="${!produtividade}">
		<%@include file="/portais/docente/menu_docente.jsp"%>
		<h2>
			<a href="${ctx}/prodocente/nova_producao.jsf">
		 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
		 	</a>
			Bolsa Obtida
		</h2>

		<h:form>
		 <div class="infoAltRem" style="width: 100%">
		  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		  <a href="${ctx}/prodocente/producao/BolsaObtida/lista.jsf" >Listar Bolsas Obtidas Cadastradas</a>
		 </div>
	    </h:form>
    </c:if>

	<c:if test="${produtividade}">
		<h2> <ufrn:subSistema/> &gt; Bolsa de Produtividade </h2>

		<h:form>
		 <div class="infoAltRem" style="width: 100%">
		  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		  <a href="${ctx}/prodocente/producao/BolsaObtida/lista_produtividade.jsf" >Listar Bolsistas de Produtividade Cadastrados</a>
		 </div>
	    </h:form>
	</c:if>

	<h:form id="form">
		<table class="formulario" width="95%">
			<caption class="listagem">Cadastro de Bolsas Obtidas</caption>
			<h:inputHidden value="#{bolsaObtida.confirmButton}" />
			<h:inputHidden value="#{bolsaObtida.obj.id}" />
			<h:inputHidden value="#{bolsaObtida.produtividade}" />
			<h:inputHidden value="#{bolsaObtida.obj.validado}" />

			<c:if test="${produtividade}">
				<h:inputHidden value="#{bolsaObtida.obj.instituicaoFomento.id}" />
			</c:if>

			<tr>
				<th class="required" width="30%">Ano de Referência:</th>
				<td>
					 <h:inputText id="anoReferencia" value="#{bolsaObtida.obj.anoReferencia}" size="5" maxlength="4" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>

			<tr>
				<th class="required" nowrap="nowrap">Data da Produção:</th>
				<td>
	    			 <t:inputCalendar value="#{ bolsaObtida.obj.dataProducao }" id="dataPublicacao" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" 
	    				renderPopupButtonAsImage="true" readonly="#{ bolsaObtida.readOnly }" 
	    				disabled="#{ bolsaObtida.readOnly }">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>								
			</tr>

			<c:choose>
				<c:when test="${produtividade}">
					<tr>
						<th class="required">Docente:</th>
						<td>
							<h:inputHidden id="idDocente" value="#{bolsaObtida.obj.servidor.id}"></h:inputHidden>
							<h:inputText id="nomeDocente"	value="#{bolsaObtida.obj.servidor.pessoa.nome}" style="width: 80%;"/>
							<ajax:autocomplete
								source="form:nomeDocente" target="form:idDocente"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
								parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicator" style="display:none; ">
								<img src="/sigaa/img/indicator.gif" />
							</span>
						</td>
					</tr>
					<tr>
						<th class="required">Tipo de Bolsa:</th>
						<td>
							<h:selectOneMenu value="#{bolsaObtida.obj.tipoBolsa.id}"
								disabled="#{bolsaObtida.readOnly}"
								disabledClass="#{bolsaObtida.disableClass}" style="width: 80%;">
								<f:selectItem itemValue="0" itemLabel=" SELECIONE O TIPO DE BOLSA " />
								<f:selectItems value="#{tipoBolsa.allProdutividadeCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th>Docente:</th>
						<td>
							<b><h:outputText value="#{bolsaObtida.usuarioLogado.pessoa.nome}" /></b>
						</td>
					</tr>
					<tr>
						<th class="required">Instituição de Fomento:</th>

						<td><h:selectOneMenu style="width: 260px;" value="#{bolsaObtida.obj.instituicaoFomento.id}"
							disabled="#{bolsaObtida.readOnly}" disabledClass="#{bolsaObtida.disableClass}">
							<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
							<f:selectItems value="#{instituicaoFomento.allCombo}"/>
						</h:selectOneMenu></td>
					</tr>
					<tr>
						<th  class="required">Tipo de bolsa obtida:</th>
						<td>
							<h:selectOneMenu value="#{bolsaObtida.obj.tipoBolsa.id}"
								disabled="#{bolsaObtida.readOnly}"
								disabledClass="#{bolsaObtida.disableClass}" style="width: 80%;">
								<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
								<f:selectItems value="#{tipoBolsa.allAtivoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>

			<tr>
				<th class="required">Área de Conhecimento:</th>

				<td><h:selectOneMenu style="width: 80%;" value="#{bolsaObtida.obj.area.id}"
					disabled="#{bolsaObtida.readOnly}" disabledClass="#{bolsaObtida.disableClass}"
					valueChangeListener="#{bolsaObtida.changeArea}" id="area">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
				    <a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu style="width: 80%;" value="#{bolsaObtida.obj.subArea.id}"
					disabled="#{bolsaObtida.readOnly}" disabledClass="#{bolsaObtida.disableClass}" id="subarea">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{bolsaObtida.subArea}"/>
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th  class="required">Período:</th>
				<td>
					
	    			 <h:inputText value="#{ bolsaObtida.mesInicial }" id="mesInicio" size="2" maxlength="2" onkeyup="formatarInteiro(this)"/> /
	    			 <h:inputText value="#{ bolsaObtida.anoInicial }" id="anoInicio" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
					 <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span>
					a
	    			 <h:inputText value="#{ bolsaObtida.mesFinal }" id="mesFinal" size="2" maxlength="2" onkeyup="formatarInteiro(this)"/> /
	    			 <h:inputText value="#{ bolsaObtida.anoFinal }" id="anoFinal" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
					 <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span>
				</td>
			</tr>
			<tr>
				<th>Informações Complementares:</th>
				<td>
				<h:inputTextarea value="#{bolsaObtida.obj.informacao}" id="informacao" rows="4" style="width: 90%;"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{bolsaObtida.confirmButton}" action="#{bolsaObtida.cadastrar}" />
						<h:commandButton immediate="true" value="Cancelar" action="#{bolsaObtida.cancelar}" 
										 onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>