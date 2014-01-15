<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<h2><ufrn:subSistema /> > RELATÓRIO DE DADOS BANCÁRIOS DE MONITORES</h2>

	<h:outputText value="#{consultarMonitor.create}"/>

	<c:set var="monitores" value="${consultarMonitor.monitores}"/>

	<c:if test="${empty monitores}">
	<center><i> Nenhum monitor localizado </i></center>
	</c:if>


	<c:if test="${not empty monitores}">

	 <table class="listagem">
	    <caption>Monitores Encontrados (${ fn:length(monitores) })</caption>
	 	<tbody>
       	<c:forEach items="${monitores}" var="monitor" varStatus="status">
               <tr>
					<td>
					 <table class="formulario" width="100%">

		                    <tr><td colspan="2" class="subFormulario">Dados Pessoais</td></tr>
		                    <tr><td width="20%">Matricula:</td>		<td>${monitor.discente.matricula}</td></tr>
		                    <tr><td>CPF:</td>						<td><ufrn:format type="cpf_cnpj" name="monitor" property="discente.pessoa.cpf_cnpj"></ufrn:format></td></tr>
		                    <tr><td>Nome:</td>						<td><b>${monitor.discente.nome} </b></td></tr>                    	
		                    <tr><td>Data Nascimento:</td>			<td><fmt:formatDate value="${monitor.discente.pessoa.dataNascimento}" pattern="dd/MM/yyyy" /></td></tr>
		                    <tr><td>Sexo:</td>						<td>${monitor.discente.pessoa.sexo}</td></tr> 	
		                    <tr><td>Curso:</td>						<td>${monitor.discente.curso.descricao}</td> </tr>                	                    	
		                    	
		                    <tr><td colspan="2" class="subFormulario">Endereço</td></tr>		                    	
		                    <tr><td>Endereço:</td>				<td>${monitor.discente.pessoa.enderecoContato.logradouro}</td></tr>                 	                    	                    	
		                    <tr><td>Número:</td>				<td>${monitor.discente.pessoa.enderecoContato.numero}</td></tr>                   
		                    <tr><td>Complemento:</td>			<td>${monitor.discente.pessoa.enderecoContato.complemento}</td></tr>                 	                    	                    	                    
		                    <tr><td>Bairro:</td>				<td>${monitor.discente.pessoa.enderecoContato.bairro}</td></tr>                    					                    
		                    <tr><td>CEP:</td>					<td>${monitor.discente.pessoa.enderecoContato.cep}</td></tr>                     
		                    <tr><td>Município:</td>				<td>${monitor.discente.pessoa.enderecoContato.municipio.nome}</td></tr>                                         
		                    <tr><td>UF:</td>					<td>${monitor.discente.pessoa.enderecoContato.municipio.unidadeFederativa.sigla}</td></tr>                                                             
		                    <tr><td>Telefone Fixo:</td>			<td>${monitor.discente.pessoa.telefone}</td></tr>                          
		                    <tr><td>Telefone Celular:</td>		<td>${monitor.discente.pessoa.celular}</td></tr>                                                                                 
		                    <tr><td>E-mail:</td>				<td>${monitor.discente.pessoa.email}</td></tr>                                                                                                     
		                    
		                    
   		                    <tr><td colspan="2" class="subFormulario">Dados Bancários</td></tr>
		                    <tr><td>Banco:</td>		<td>${monitor.banco.denominacao}</td></tr>
		                    <tr><td>Agência:</td>	<td>${monitor.agencia}</td></tr>                    
		                    <tr><td>Conta:</td>		<td>${monitor.conta}</td></tr>  
		                    <tr><td>Operação:</td>	<td>${monitor.operacao}</td></tr>                    
		                    
   		                    <tr><td colspan="2" class="subFormulario">Dados da Monitoria</td></tr>
   		                    <tr><td>Projeto:</td>		<td>${monitor.projetoEnsino.titulo}</td></tr>
		                    <tr><td>Tipo Vínculo:</td>	<td>${monitor.tipoVinculo.descricao}</td></tr>
		                    <tr><td>Situação:</td>		<td>${(monitor.ativo) ? '<font color=blue>ATIVO</font>':'<font color=red>INATIVO</font>'} - ${monitor.situacaoDiscenteMonitoria.descricao}</td></tr>
		                    
                    </table>
                    </td>
              </tr>
              <tr>
              	<td>
              		<hr/>
              	</td>
              </tr>		                    
          </c:forEach>
	 	</tbody>
	 	
	 	
	 	<tfoot>
			<tr>
				<td colspan="6" align="center">
						<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
				</td>
			</tr>
		</tfoot>	 	
	 	
	 </table>
	
	</c:if>



</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>