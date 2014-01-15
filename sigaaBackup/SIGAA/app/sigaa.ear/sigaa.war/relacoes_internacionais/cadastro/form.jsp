<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Item para Tradução</h2>

	<div class="descricaoOperacao">
		Esta operação destina-se aos usuários com permissão de administrador do SIGAA. 
		Nesta tela selecione o Objeto, que o atributo pertença e siga o cadastro do item que possuirá seus valores traduzidos posteriormente no sistema. 
	</div>

	<center>
			<h:form>
			<div class="infoAltRem" style="text-align: center width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  			<h:commandLink value="Listar Atributos" action="#{itemTraducaoMBean.listar}"/>
			</div>
			</h:form>
	</center>


	<table class="formulario" width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Atributo do Objeto para tradução</caption>
			<h:inputHidden value="#{itemTraducaoMBean.confirmButton}" />
			<h:inputHidden value="#{itemTraducaoMBean.obj.id}" />
			<tr>
				<th class="obrigatorio">Entidade:</th>
				<td>
					<h:selectOneMenu id="entidade" value="#{itemTraducaoMBean.obj.entidade.id}">
						<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
						<f:selectItems value="#{entidadeTraducaoMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText size="60" maxlength="80" value="#{itemTraducaoMBean.obj.nome}"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Atributo:
				<ufrn:help img="/img/ajuda.gif">
					Informar este valor conforme declarado na classe da entidade.
				</ufrn:help></th>
				<td><h:inputText size="60" maxlength="80" value="#{itemTraducaoMBean.obj.atributo}"/></td>
			</tr>
			<tr>
				<th>Idiomas:
				<ufrn:help img="/img/ajuda.gif">
					Informar possíveis idiomas que o item será traduzido, separados por vírgula ex: (en,pt,fr).
				</ufrn:help></th>
				<td><h:inputText size="60" maxlength="80" value="#{itemTraducaoMBean.obj.idiomas}"/></td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{itemTraducaoMBean.obj.tipoAreaTexto}"/></th>
				<th style="text-align: left;" valign="top">
					Preenchimento das traduções em campos de Texto
					<ufrn:help img="/img/ajuda.gif" width="300">
						Preencher este campo quando o texto para tradução do item for superior a 80 caracteres. 
						<br/>Ao sinalizar este campo, o texto de tradução será solicitado em uma área de texto. 
					</ufrn:help>
				</th>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{itemTraducaoMBean.confirmButton}" action="#{itemTraducaoMBean.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{itemTraducaoMBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>