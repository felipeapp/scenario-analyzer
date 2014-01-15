<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="usuarioExternoBibliotecaMBean" />

	<h2><ufrn:subSistema /> &gt; Usuário Externo da Biblioteca &gt; Verificação do Número do Passaporte</h2>

	<h:form>
	
	<div class="descricaoOperacao">
		<p> <strong>ATENÇÃO : </strong> Foram detectadas outras pessoas cadastradas com o mesmo número de passaporte, por favor confirme se o usuário que se está 
		tentando cadastrar já não existe no sistema.
		</p>
		<p>
		Caso se trate de um novo usuário, prossiga o cadastro com a opção: <strong> <i> Incluir novo Usuário </i> </strong>
		</p>
		<p><i>
		Isso se deve porque, diferentemente do CPF, o número do passaporte não é um identificador único.
		</i></p>
	</div>
	
	<div class="infoAltRem" style="margin-top: 10px">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
			Selecionar Usuário
		<br/>
		
	</div>
	
	<table class="formulario" width="100%">
	
		<caption>Selecione dentre os Usuários Abaixo</caption>
	
		<thead>
			<tr>
				<th>Passaporte</th>
				<th>Nome</th>
				<th>Data de Nascimento</th>
				<th>Nome da Mãe</th>
				<th>Nome do Pai</th>
				<th>País</th>
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
						<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar Usuário" />
						<f:param name="idPessoaSelecionada" value="#{pessoa.id}"/>
					</h:commandLink>
				
				</td>
			</tr>
		</c:forEach>
		
	
		<tfoot>
			<tr>
				<td colspan="7">
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.voltarFormularioPessoaExtrageira}" value="<< Voltar " />
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.prosseguirCadastroPessoaExtrageira}" value="Incluir novo Usuário >>" />
					<h:commandButton action="#{usuarioExternoBibliotecaMBean.cancelar}" value="Cancelar" immediate="true"  onclick="#{confirm}" />
				</td>
		</tfoot>
	</table>
			
			
	</h:form>
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	