<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="usuarioExternoBibliotecaMBean" />

	<h2><ufrn:subSistema /> &gt; Usu�rio Externo da Biblioteca &gt; Verifica��o do N�mero do Passaporte</h2>

	<h:form>
	
	<div class="descricaoOperacao">
		<p> <strong>ATEN��O : </strong> Foram detectadas outras pessoas cadastradas com o mesmo n�mero de passaporte, por favor confirme se o usu�rio que se est� 
		tentando cadastrar j� n�o existe no sistema.
		</p>
		<p>
		Caso se trate de um novo usu�rio, prossiga o cadastro com a op��o: <strong> <i> Incluir novo Usu�rio </i> </strong>
		</p>
		<p><i>
		Isso se deve porque, diferentemente do CPF, o n�mero do passaporte n�o � um identificador �nico.
		</i></p>
	</div>
	
	<div class="infoAltRem" style="margin-top: 10px">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
			Selecionar Usu�rio
		<br/>
		
	</div>
	
	<table class="formulario" width="100%">
	
		<caption>Selecione dentre os Usu�rios Abaixo</caption>
	
		<thead>
			<tr>
				<th>Passaporte</th>
				<th>Nome</th>
				<th>Data de Nascimento</th>
				<th>Nome da M�e</th>
				<th>Nome do Pai</th>
				<th>Pa�s</th>
				<th> </th>
			</tr>
		</thead>
	
		<c:forEach var="pessoa" items="#{usuarioExternoBibliotecaMBean.pessoasComPassaporteDuplicados}">
			<tr>
				<td>${pessoa.passaporte}</td>
				<td>${pessoa.nome}</td>
				<td> <ufrn:format type="data" valor="${pessoa.dataNascimento}" /> </td>
				<td>${pessoa.nomeMae}</td>
				<td>${pessoa.nomePai}</td>
				<td>${pessoa.pais.nome}</td>
				<td> 
				
					<h:commandLink  actionListener="#{usuarioExternoBibliotecaMBean.selecionouPessoaExtrageiraCadastrada}">
						<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar Usu�rio" />
						<f:param name="idPessoaSelecionada" value="#{pessoa.id}"/>
					</h:commandLink>
				
				</td>
			</tr>
		</c:forEach>
		
	
		<tfoot>
			<tr>
				<td colspan="7">
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.voltarFormularioPessoaExtrageira}" value="<< Voltar " />
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.prosseguirCadastroPessoaExtrageira}" value="Incluir novo Usu�rio >>" />
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.cancelar}" value="Cancelar" immediate="true"  onclick="#{confirm}" />
				</td>
		</tfoot>
	</table>
			
			
	</h:form>
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	