		 <caption>Corpo Docente do Curso</caption>
		   <thead>
				<tr>
					<td><b>SIAPE</b></td>
					<td><b>Nome</b></td>
					<td><b>Titulação</b></td>
					<td><b>Instituição</b></td>
				</tr>
		   </thead>
		   
		   <c:forEach items="${cursoLatoMBean.obj.cursosServidores}" var="cursoServidor" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	            <c:choose>
	            	<c:when test="${cursoServidor.externo}">
						<td> - </td>
	                    <td>${cursoServidor.docenteExterno.pessoa.nome}</td>
	                    <td>${cursoServidor.docenteExterno.formacao.denominacao}</td>
	                    <td>${cursoServidor.docenteExterno.instituicao.sigla}</td>
	                </c:when>
	                <c:otherwise>
	                	<td>${cursoServidor.servidor.siape}</td>
	                    <td>${cursoServidor.servidor.pessoa.nome}</td>
	                    <td>${cursoServidor.servidor.formacao.denominacao}</td>
	                    <td>${ configSistema['siglaInstituicao'] }</td>
	                </c:otherwise>
	            </c:choose>
	            </tr>
	        </c:forEach>
		  