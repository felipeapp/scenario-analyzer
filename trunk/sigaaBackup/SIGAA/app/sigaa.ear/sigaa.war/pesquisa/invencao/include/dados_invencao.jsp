<style>
	table.visualizacao tr td.subFormulario {
		padding: 3px 0 3px 20px;
	}
	p.corpo {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>
	<tr>
		<td colspan="2" class="subFormulario"> Dados Básicos </td>
	</tr>

	<tr>
		<th width="30%"><b>Código:<b/></th>
		<td>
			<h:outputText id="codigo" value="#{invencao.obj.codigo}" />
		</td>
	</tr>
	<tr>
		<th width="30%"><b>Centro:<b/></th>
		<td>
			<h:outputText id="centro" value="#{invencao.obj.centro.nome}" />
		</td>
	</tr>
	<tr>
		<th width="30%"><b>Tipo:<b/></th>
		<td>
			<h:outputText id="tipo" value="#{invencao.obj.tipo.descricao}" />
		</td>
	</tr>

	<tr>
		<th><b>Palavras-chave (Português):</b></th>
		<td>
			<h:outputText id="palavrasChavePortugues" value="#{invencao.obj.palavrasChavePortugues}"/>
		</td>
	</tr>

	<tr>
		<th><b>Palavras-chave (Inglês):</b></th>
		<td>
			<h:outputText id="palavrasChaveIngles" value="#{invencao.obj.palavrasChaveIngles}"/>
		</td>
	</tr>
			
	<tr>
		<th><b>Área de Conhecimento:</b></th>
		<td>
			<h:outputText id="areaConhecimento" value="#{invencao.obj.areaConhecimentoCnpq.nome}"/>
		</td>
	</tr>	
	
	<c:if test="${ not empty invencao.obj.arquivos }">
		<tr>
			<td colspan="2" class="subFormulario"> Revelação da Invenção </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableRevelacoes" value="#{invencao.obj.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
						<h:outputText value="#{arquivo.descricao}" rendered="#{arquivo.categoria == 2}"/>
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Data</f:verbatim></f:facet>
						<h:outputText value="#{arquivo.data}" rendered="#{arquivo.categoria == 2}"/>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
	</c:if>

	<c:if test="${ not empty invencao.obj.utilizacaoMaterial }">
		<tr>
			<td colspan="2" class="subFormulario"> Utilização de material biológico/genético </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="utilizacao-material" value="#{invencao.obj.utilizacaoMaterial}" />
				</p>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${ not empty invencao.obj.utilizacaoSoftware }">
		<tr>
			<td colspan="2" class="subFormulario"> Utilização de software </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="utilizacao-software" value="#{invencao.obj.utilizacaoSoftware}" />
				</p>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${ not empty invencao.obj.financiamentos }">
		<tr>
			<td class="subFormulario" colspan="2"> Participação Institucional </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableFinanciamentos" value="#{invencao.obj.financiamentos}" var="financiamento" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Entidade Financiadora</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.entidadeFinanciadora.nome}" />
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Número do Processo do Convênio</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.numeroProcessoConvenio}"/>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
	</c:if>
	
	<tr>
		<td colspan="2" class="subFormulario"> Autores da Invenção </td>
	</tr>
	<tr>
		<td colspan="2">
 		  <table align="center" width="100%" class="listagem">		  
			   <c:forEach items="#{invencao.obj.inventores}" var="inventor" varStatus="status">
					<tr>
						<td align="justify" colspan="2"><b>Nome</b></td> 	
						<td align="justify" colspan="2"><b>Categoria</b></td> 		
						<td align="justify" colspan="2"><b>Departamento / Curso / Instituição</b></td>
					</tr>
					<tr>
						<td align="justify" colspan="2"> ${inventor.pessoa.nome}</td>
						<td align="justify" colspan="2"> ${inventor.categoriaMembro.descricao}</td>
						<td align="justify" colspan="2"> ${inventor.origem}</td>
					</tr>
					<tr >
						<td align="justify" colspan="2"><b>CPF</b></td> 		
						<td align="justify" colspan="2"><b>R.G</b></td>
						<td align="justify" colspan="2"><b>Nacionalidade</b></td>
					</tr>
					<tr >
						<td align="justify" colspan="2"><ufrn:format type="cpf_cnpj" valor="${inventor.pessoa.cpf_cnpj}" /></td>
						<td align="justify" colspan="2"> ${inventor.pessoa.identidade}</td>
						<td align="justify" colspan="2"> ${inventor.pessoa.pais.nacionalidade}</td>
					</tr>
					<tr >
						<td align="justify" colspan="2"><b>Endereço</b></td>
						<td align="justify" colspan="2"><b>Telefone</b></td>
						<td align="justify" colspan="2"><b>Email</b></td>
					</tr>
					<tr >
						<td align="justify" colspan="2"> ${inventor.pessoa.endereco}</td>
						<td align="justify" colspan="2"> ${inventor.pessoa.telefone}</td>
						<td align="justify" colspan="2"> ${inventor.pessoa.email}</td>
					</tr>
					<tr>
						<td colspan="2">
						<br />
						</td>
					</tr>
			   </c:forEach>
		   </table>
		</td>
	</tr>
	
	<c:if test="${ not empty invencao.obj.historico }">
		<tr>
			<td class="subFormulario" colspan="2"> Histórico da Notificação de Invenção </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableHistorico" value="#{invencao.obj.historico}" var="entrada_" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Data</f:verbatim></f:facet>
						<h:outputText value="#{entrada_.data}" >
							<f:convertDateTime type="both"/>
						</h:outputText>
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{entrada_.statusString}"/>
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Usuário</f:verbatim></f:facet>
						<h:outputText value="#{entrada_.registroEntrada.usuario.pessoa.nome}"/>
						<f:verbatim>&nbsp;<i>(</f:verbatim> 
							<h:outputText value="#{entrada_.registroEntrada.usuario.login}"/> 
						<f:verbatim>)</i></f:verbatim>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
	</c:if>