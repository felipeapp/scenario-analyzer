<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h:form  id="formInteressadoProcessoSeletivo">
	<a4j:keepAlive beanName="interessadoProcessoSeletivo"/>
	<h2 class="title"><ufrn:subSistema /> > Processo Seletivo</h2>
	
	<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	Esta tela tem a funcionalidade de listar os responsáveis pela divulgação dos processos seletivos. A tela também oferece a opção de cadastrar ou remover um responsável. 
	</div>
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</div>
	</center>
	
	<table class="formulario" width="50%">
		<caption>Cadastrar Novo Interessado </caption>
		<tr>
			<th width="30%" class="obrigatorio">Interessado:</th>
			<td>
				<a4j:region>
				<h:inputText value="#{interessadoProcessoSeletivo.obj.pessoa.nome}" id="nomePessoa" size="59"/>
				<h:inputHidden value="#{interessadoProcessoSeletivo.obj.pessoa.id}" id="idPessoa"/>		
				<ajax:autocomplete source="formInteressadoProcessoSeletivo:nomePessoa" target="formInteressadoProcessoSeletivo:idPessoa"
									baseUrl="/sigaa/ajaxPessoa" className="autocomplete"
									indicator="indicator" minimumCharacters="3" parameters="tipo=F"
									parser="new ResponseXmlToHtmlListParser()" />

				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</a4j:region>
			</td>
		</tr>	
		<tfoot>
			<tr>
				<td colspan=2 align="center">
					<h:commandButton action="#{interessadoProcessoSeletivo.cadastrar}" value="Cadastrar"></h:commandButton>
				</td>
			</tr>
		</tfoot>		
	</table>
	
	<c:if test="${ not empty interessadoProcessoSeletivo.listagemInteressados }">
		<br/>
			<table class="formulario" width="80%">
				<caption class="listagem">Lista de Interessados</caption>
				<thead>
					<tr>
						<th width="30%">Nome</th>
						<th width="20%">E-mail</th>
						<th></th>
					</tr>
				</thead>
				
				<tbody>
				<a4j:repeat value="#{interessadoProcessoSeletivo.listagemInteressados}" var="i" rowKeyVar="linha" >
						
					<tr class="<h:outputText value="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />">
							
						<td>
							<h:outputText value="#{i.pessoa.nome}" />
						</td>	
						
						<td align="left">
							<h:outputText value="#{i.pessoa.email}" />
						</td>	
							
						<td align="center" width="3%">
							<h:commandLink action="#{interessadoProcessoSeletivo.inativar}" title="Remover" onclick="#{ confirmDelete }">
								<h:graphicImage value="/img/delete.gif" />
								<f:param name="id" value="#{ i.id }" />
							</h:commandLink>
						</td>

	
					</tr>
				</a4j:repeat>
				</tbody>
			</table>
			
		</c:if>		
		
		<c:if test="${ empty interessadoProcessoSeletivo.listagemInteressados }">
			<p style="font-weight:bold;text-align:center;color:red;margin:20px;">Nenhum item foi encontrado</p>
		</c:if>
</h:form>

</f:view>

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function pessoaOnFocus(e) {
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
