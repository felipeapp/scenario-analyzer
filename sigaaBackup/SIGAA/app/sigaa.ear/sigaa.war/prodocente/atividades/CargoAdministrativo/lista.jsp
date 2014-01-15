<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cargo Administrativo</h2>
	<h:form id="form" >
	 <a4j:keepAlive beanName="cargoAdministrativo"/>
		<center>
			<table class="formulario" width="50%">
			<caption class="listagem">Buscar Cargo Administrativo</caption>
				<tr>
					<th class="required">Docente:</th>
					<td>
						<h:inputHidden id="id" value="#{cargoAdministrativo.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{cargoAdministrativo.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>
				</tr>
			  <tfoot>
				<tr>
					<td align="center" colspan="3"> <h:commandButton action="#{cargoAdministrativo.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
  			  </tfoot>
			</table>
		</center>

		<br>
		<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		</center>
		<br>
		
	</h:form>
	<c:set value="${cargoAdministrativo.allAtividades}" var="atividades" />
	<c:if test="${not empty atividades}">
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{cargoAdministrativo.preCadastrar}" value="Cadastrar Novo Cargo Administrativo"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Cargo Administrativo
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Cargo Administrativo<br/>
		</div>
	</h:form>
	<h:outputText value="#{cargoAdministrativo.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Cargos Administrativos</caption>
		<thead>

			<td>Docente</td>
			<td style="text-align: center;">Data de Início</td>
			<td style="text-align: center;">Data do Fim</td>
			<td>Portaria</td>
			<td style="text-align: center;">Data da Portaria</td>
			<td>Portaria Final</td>
			<td style="text-align: center;">Data Portaria Fim</td>
			<td>Cargo</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${atividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.servidor.pessoa.nome}</td>
				<td align="center"><ufrn:format type="data" name="item" property="dataInicio"/></td>
				<td align="center"><ufrn:format type="data" name="item" property="dataFim"/></td>
				<td>${item.portaria}</td>
				<td align="center"><ufrn:format type="data" name="item" property="dataPortaria"/></td>
				<td>${item.portariaFinal}</td>
				<td align="center"><ufrn:format type="data" name="item" property="dataPortariaFim"/></td>
				<td>${item.designacaoCargo.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{cargoAdministrativo.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{cargoAdministrativo.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
