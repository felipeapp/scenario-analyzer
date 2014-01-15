<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Entidade para Tradução</h2>

	<div class="descricaoOperacao">
		Esta operação destina-se aos usuários com permissão de administrador do SIGAA. 
		Nesta informe os valores da classe do Objeto, que terá seus valores traduzidos posteriormente no sistema. 
	</div>

	<center>
		<h:form>
		<div class="infoAltRem" style="text-align: center width: 100%">
			<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
  			<h:commandLink value="Listar Entidades" action="#{entidadeTraducaoMBean.listar}"/>
		</div>
		</h:form>
	</center>


	<table class="formulario" width="100%">
		<h:form>
			<caption class="listagem">Cadastro da Entidade do Objeto para Tradução</caption>
			<h:inputHidden value="#{entidadeTraducaoMBean.confirmButton}" />
			<h:inputHidden value="#{entidadeTraducaoMBean.obj.id}" />
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText size="60" maxlength="80" value="#{entidadeTraducaoMBean.obj.nome}"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Classe:
				<ufrn:help img="/img/ajuda.gif" width="450">
					Informar este valor conforme declarado no pacote do projeto de tradução de documentos. (ex: br.ufrn.sigaa.ensino.dominio.ComponenteCurricular)
				</ufrn:help></th>
				<td><h:inputText size="60" maxlength="80" value="#{entidadeTraducaoMBean.obj.classe}"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{entidadeTraducaoMBean.confirmButton}" action="#{entidadeTraducaoMBean.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{entidadeTraducaoMBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>