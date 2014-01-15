<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{solicitacaoApoioNee.create }"></h:outputText>
	<h2 class="title"><ufrn:subSistema /> > Visualizar Solicitação de Apoio a NEE</h2>

		<table class="visualizacao" width="100%">
			<caption class="formulario">Dados da Solicitação de Apoio a NEE</caption>
			<tr>
				<th width="35%">Nome:</th>
				<td><h:outputText id="nome" value="#{solicitacaoApoioNee.obj.discente.nome}" /> </td>
			</tr>
		
			<tr>
				<th>Data Nascimento:</th>
				<td><h:outputText id="nivel" value="#{solicitacaoApoioNee.obj.discente.pessoa.dataNascimento}" /></td>
			</tr>
			<tr>
				<th>Sexo:</th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.sexo eq 'F' ? 'Feminino' : 'Masculino'}
				</td>
			</tr>
			<tr>
				<th>Matrícula:</th>
				<td>
					<h:outputText id="titulacao" value="#{solicitacaoApoioNee.obj.discente.matricula}"/>
				</td>
			</tr>
			<c:choose>
				<c:when test="${solicitacaoApoioNee.obj.discente.graduacao}">
					<tr>
						<th> Curso: </th>
						<td colspan="3"> ${solicitacaoApoioNee.obj.discente.matrizCurricular.descricao}</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th> Curso: </th>
						<td colspan="3"> ${solicitacaoApoioNee.obj.discente.curso.descricao}</td>
					</tr>
				</c:otherwise>
			</c:choose>
			<tr>
				<th>Coordenador de Curso:</th>
				<td><h:outputText id="coordenador" value="#{solicitacaoApoioNee.nomeCoordenadorDiscente}" /> </td>
			</tr>
			
			<tr>
				<th>Orientador Acadêmico:</th>
				<td><h:outputText id="orientador" value="#{solicitacaoApoioNee.nomeOrientador}" /> </td>
			</tr>
			
			<tr>
				<th>Forma de Ingresso:</th>
				<td><h:outputText id="formaIngresso" value="#{solicitacaoApoioNee.obj.discente.formaIngresso.descricao}" /> </td>
			</tr>
			<tr>
				<th> Ano/Período de Ingresso: </th>
				<td colspan="3">${solicitacaoApoioNee.obj.discente.anoIngresso}.${solicitacaoApoioNee.obj.discente.periodoIngresso}</td>
			</tr>
			<tr>
				<th> Ano/Período Atual: </th>
				<td colspan="3">${solicitacaoApoioNee.calendarioVigente.ano}.${solicitacaoApoioNee.periodoAtual}</td>
			</tr>
			<tr>
				<th>Endereço: </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.tipoLogradouro}
					${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.logradouro},
					nº ${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.numero}
				</td>
			</tr>	
			<tr>	
				<th>
					<h:outputLabel value="Bairro:" rendered="#{solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.bairro != null}" />
				</th>
				<td>
					<h:outputText id="bairro" value="#{solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.bairro}" 
						rendered="#{solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.bairro != null}"/>
				</td>
			</tr>
			<tr>	
				<th>Cidade: </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.municipio.nome}
				</td>
			</tr>
			<tr>	
				<th>Estado: </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
				</td>
			</tr>
			<tr>
				<th>CEP: </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.cep}
				</td>
			</tr>
			<tr>	
				<th>Telefone(s): </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.telefone}
					<c:if test="${solicitacaoApoioNee.obj.discente.pessoa.telefone != '' and solicitacaoApoioNee.obj.discente.pessoa.celular != ''}">/</c:if> 
					${solicitacaoApoioNee.obj.discente.pessoa.celular}
				</td>
			</tr>
			<tr>
				<th>E-mail: </th>
				<td>
					${solicitacaoApoioNee.obj.discente.pessoa.email}
				</td>
			</tr>
			<tr>
				<th>Data de Solicitação: </th>
				<td>
					<h:outputText value="#{solicitacaoApoioNee.obj.dataCadastro}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
				</td>
			</tr>
			<tr>
				<th>Situação da Solicitação: </th>
				<td>
					${solicitacaoApoioNee.obj.statusAtendimento.denominacao}
				</td>
			</tr>
			
			<tr>
				<th colspan="1">Tipo de Necessidade Educacional Especial: </th>
				<td colspan="2">
				
					<c:forEach items="#{solicitacaoApoioNee.necessidadesEspeciaisDiscente}" var="item">
						${item.tipoNecessidadeEspecial.descricao} <br/>
					</c:forEach>
				
				</td>
			</tr>
			<tr>
				<th> Justificativa para solicitação de apoio da CAENE: </th>
				<td colspan="3">
					<h:outputText value="#{solicitacaoApoioNee.obj.justificativaSolicitacao}" id="justificativa" />
				</td>		
			</tr>
			
			<tr>
				<caption class="subformulario">Parecer</caption>
				<th colspan="1">
					<h:outputText>Parecer Técnico da CAENE:</h:outputText></th>
				<td colspan="3">
					<h:outputText value="#{solicitacaoApoioNee.obj.parecerComissao}" id="parecerComissao" />
				</td>
			</tr>	
			
		</table>
		
		<br>
		<center>
			<h:form>
				<input type="button" value="<< Voltar"  onclick="javascript: history.back();" id="voltar" />
			</h:form>
		</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
